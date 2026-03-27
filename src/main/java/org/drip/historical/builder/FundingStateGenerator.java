
package org.drip.historical.builder;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.analytics.support.Helper;
import org.drip.historical.calibration.DepositInstrumentSuite;
import org.drip.historical.calibration.FixFloatInstrumentSuite;
import org.drip.historical.calibration.FundingMarkSuite;
import org.drip.historical.calibration.FuturesInstrumentSuite;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.ComposableFixedUnitSetting;
import org.drip.param.period.ComposableFloatingUnitSetting;
import org.drip.param.period.CompositePeriodSetting;
import org.drip.param.period.UnitCouponAccrualSetting;
import org.drip.param.valuation.CashSettleParams;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.rates.FixFloatComponent;
import org.drip.product.rates.SingleStreamComponent;
import org.drip.product.rates.Stream;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.inference.LatentStateStretchSpec;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmidrip.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmidrip.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmidrip.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmidrip.github.io/DROP-Computational-Core/
 * 
 * 	DROP Product Core implements libraries for the following:
 * 	- Fixed Income Analytics
 * 	- Loan Analytics
 * 	- Transaction Cost Analytics
 * 
 * 	DROP Portfolio Core implements libraries for the following:
 * 	- Asset Allocation Analytics
 *  - Asset Liability Management Analytics
 * 	- Capital Estimation Analytics
 * 	- Exposure Analytics
 * 	- Margin Analytics
 * 	- XVA Analytics
 * 
 * 	DROP Computational Core implements libraries for the following:
 * 	- Algorithm Support
 * 	- Computation Support
 * 	- Function Analysis
 *  - Graph Algorithm
 *  - Model Validation
 * 	- Numerical Analysis
 * 	- Numerical Optimizer
 * 	- Spline Builder
 *  - Statistical Learning
 * 
 * 	Documentation for DROP is Spread Over:
 * 
 * 	- Main                     => https://lakshmidrip.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmiDRIP/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmiDRIP/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmiDRIP/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmidrip.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmiDRIP/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmidrip.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmidrip.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmiDRIP/DROP/issues
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   	you may not use this file except in compliance with the License.
 *   
 *  You may obtain a copy of the License at
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  	distributed under the License is distributed on an "AS IS" BASIS,
 *  	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  
 *  See the License for the specific language governing permissions and
 *  	limitations under the License.
 */

/**
 * <i>FundingStateGenerator</i> generates the T1/T2 Straight Up/Rolled Funding States. It provides the
 * 	following Functionality:
 *
 *  <ul>
 * 		<li><i>FundingStateGenerator</i> Constructor</li>
 * 		<li>Retrieve the Customization Settings</li>
 * 		<li>Construct the Array of Deposit Instruments</li>
 * 		<li>Construct the Array of Futures Instruments</li>
 * 		<li>Construct the Array of Fix-Float Instruments</li>
 * 		<li>Generate the T0 EOD Funding State Evaluation Results</li>
 * 		<li>Generate the T1 SOD Evaluated Funding State</li>
 * 		<li>Generate the Map of T0 EOD and the T1 SOD Funding States</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/builder/README.md">Latent State T1/T2 Builders</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class FundingStateGenerator
{
	private FundingStateCustomization _customizationSettings = null;

	/**
	 * <i>FundingStateGenerator</i> Constructor
	 * 
	 * @param customizationSettings Customization Settings
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public FundingStateGenerator (
		final FundingStateCustomization customizationSettings)
		throws Exception
	{
		if (null == (_customizationSettings = customizationSettings)) {
			throw new Exception ("FundingStateGenerator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Customization Settings
	 * 
	 * @return Customization Settings
	 */

	public FundingStateCustomization customizationSettings()
	{
		return _customizationSettings;
	}

	/**
	 * Construct the Array of Deposit Instruments
	 * 
	 * @param effectiveDate Effective Date
	 * 
	 * @return Array of Deposit Instruments
	 */

	public SingleStreamComponent[] depositComponentArray (
		final JulianDate effectiveDate)
	{
		DepositInstrumentSuite depositInstrumentSuite = _customizationSettings.depositInstrumentSuite();

		int[] maturityDaysArray = depositInstrumentSuite.maturityDaysArray();

		String periodTenor = depositInstrumentSuite.periodTenor();

		String currency = depositInstrumentSuite.currency();

		CashSettleParams cashSettleParams = null;
		CompositePeriodSetting compositePeriodSetting = null;
		ComposableFloatingUnitSetting composableFloatingUnitSetting = null;
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[maturityDaysArray.length];

		try {
			composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
				periodTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
				null,
				ForwardLabel.Create (currency, periodTenor),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			compositePeriodSetting = new CompositePeriodSetting (
				12 / Helper.TenorToMonths (periodTenor),
				periodTenor,
				currency,
				null,
				1.,
				null,
				null,
				null,
				null
			);

			cashSettleParams = new CashSettleParams (0, currency, 0);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		for (int dayIndex = 0; dayIndex < maturityDaysArray.length; ++dayIndex) {
			try {
				depositComponentArray[dayIndex] = new SingleStreamComponent (
					"DEPOSIT_" + maturityDaysArray[dayIndex],
					new Stream (
						CompositePeriodBuilder.FloatingCompositeUnit (
							CompositePeriodBuilder.EdgePair (
								effectiveDate,
								effectiveDate.addBusDays (maturityDaysArray[dayIndex], currency)
							),
							compositePeriodSetting,
							composableFloatingUnitSetting
						)
					),
					cashSettleParams
				);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			depositComponentArray[dayIndex].setPrimaryCode (maturityDaysArray[dayIndex] + "D");
		}

		return depositComponentArray;
	}

	/**
	 * Construct the Array of Futures Instruments
	 * 
	 * @param effectiveDate Effective Date
	 * 
	 * @return Array of Futures Instruments
	 */

	public SingleStreamComponent[] futuresComponentArray (
		final JulianDate effectiveDate)
	{
		FuturesInstrumentSuite futuresInstrumentSuite = _customizationSettings.futuresInstrumentSuite();

		return SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresInstrumentSuite.componentCount(),
			futuresInstrumentSuite.currency()
		);
	}

	/**
	 * Construct the Array of Fix-Float Instruments
	 * 
	 * @param effectiveDate Effective Date
	 * 
	 * @return Array of Fix-Float Instruments
	 */

	public final FixFloatComponent[] fixFloatComponentArray (
		final JulianDate effectiveDate)
	{
		FixFloatInstrumentSuite fixFloatInstrumentSuite = _customizationSettings.fixFloatInstrumentSuite();

		String[] maturityTenorArray = fixFloatInstrumentSuite.maturityTenorArray();

		String dayCountConvention = fixFloatInstrumentSuite.dayCountConvention();

		String periodTenor = fixFloatInstrumentSuite.periodTenor();

		String currency = fixFloatInstrumentSuite.currency();

		int frequency = -1;
		CompositePeriodSetting fixedCompositePeriodSetting = null;
		ComposableFixedUnitSetting composableFixedUnitSetting = null;
		CompositePeriodSetting floatingCompositePeriodSetting = null;
		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = null;
		ComposableFloatingUnitSetting composableFloatingUnitSetting = null;
		FixFloatComponent[] fixFloatComponentArray = new FixFloatComponent[maturityTenorArray.length];

		try {
			frequency = 12 / Helper.TenorToMonths (periodTenor);

			fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
				frequency,
				dayCountConvention,
				false,
				dayCountConvention,
				false,
				currency,
				true,
				CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
			);

			composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
				periodTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				ForwardLabel.Create (currency, periodTenor),
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			);

			composableFixedUnitSetting = new ComposableFixedUnitSetting (
				periodTenor,
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
				null,
				0.,
				0.,
				currency
			);

			floatingCompositePeriodSetting = new CompositePeriodSetting (
				frequency,
				periodTenor,
				currency,
				null,
				-1.,
				null,
				null,
				null,
				null
			);

			fixedCompositePeriodSetting = new CompositePeriodSetting (
				frequency,
				periodTenor,
				currency,
				null,
				1.,
				null,
				null,
				null,
				null
			);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			try {
				fixFloatComponentArray[maturityIndex] = new FixFloatComponent (
					new Stream (
						CompositePeriodBuilder.FixedCompositeUnit (
							CompositePeriodBuilder.RegularEdgeDates (
								effectiveDate,
								periodTenor,
								maturityTenorArray[maturityIndex],
								null
							),
							fixedCompositePeriodSetting,
							fixedUnitCouponAccrualSetting,
							composableFixedUnitSetting
						)
					),
					new Stream (
						CompositePeriodBuilder.FloatingCompositeUnit (
							CompositePeriodBuilder.RegularEdgeDates (
								effectiveDate,
								periodTenor,
								maturityTenorArray[maturityIndex],
								null
							),
							floatingCompositePeriodSetting,
							composableFloatingUnitSetting
						)
					),
					cashSettleParams
				);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			fixFloatComponentArray[maturityIndex].setPrimaryCode (
				"IRS." + maturityTenorArray[maturityIndex] + "." + currency
			);
		}

		return fixFloatComponentArray;
	}

	/**
	 * Generate the T0 EOD Funding State Evaluation Results
	 * 
	 * @param fundingCalibrationSuite Funding Calibration Quote Suite
	 * 
	 * @return T0 EOD Funding State Evaluation Results
	 */

	public FundingStateEvaluation t0EOD (
		final FundingMarkSuite fundingCalibrationSuite)
	{
		if (null == fundingCalibrationSuite) {
			return null;
		}

		JulianDate t0 = fundingCalibrationSuite.asOfDate();

		DepositInstrumentSuite depositSuite = _customizationSettings.depositInstrumentSuite();

		FuturesInstrumentSuite futuresSuite = _customizationSettings.futuresInstrumentSuite();

		FixFloatInstrumentSuite fixFloatSuite = _customizationSettings.fixFloatInstrumentSuite();

		SingleStreamComponent[] depositComponentArray = depositComponentArray (t0);

		SingleStreamComponent[] futuresComponentArray = futuresComponentArray (t0);

		FixFloatComponent[] fixFloatComponentArray = fixFloatComponentArray (t0);

		String currency = depositSuite.currency();

		try {
			return new FundingStateEvaluation (
				ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
					currency,
					_customizationSettings.linearLatentStateCalibrator(),
					new LatentStateStretchSpec[] {
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							depositSuite.code(),
							depositComponentArray,
							depositSuite.calibrationMetric(),
							fundingCalibrationSuite.depositQuoteArray()
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							futuresSuite.code(),
							futuresComponentArray,
							futuresSuite.calibrationMetric(),
							fundingCalibrationSuite.futuresQuoteArray()
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							fixFloatSuite.code(),
							fixFloatComponentArray,
							fixFloatSuite.calibrationMetric(),
							fundingCalibrationSuite.fixFloatQuoteArray()
						)
					},
					new ValuationParams (t0, t0, currency),
					null,
					null,
					null,
					1.
				),
				depositComponentArray,
				futuresComponentArray,
				fixFloatComponentArray,
				fundingCalibrationSuite
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the T1 SOD Evaluated Funding State
	 * 
	 * @param t1 T1
	 * @param t0MergedDiscountForwardCurve T0 Merged Discount Forward Curve
	 * 
	 * @return T1 SOD Evaluated Funding State
	 */

	public FundingStateEvaluation t1SOD (
		final JulianDate t1,
		final MergedDiscountForwardCurve t0MergedDiscountForwardCurve)
	{
		FixFloatInstrumentSuite fixFloatInstrumentSuite = _customizationSettings.fixFloatInstrumentSuite();

		DepositInstrumentSuite depositInstrumentSuite = _customizationSettings.depositInstrumentSuite();

		FuturesInstrumentSuite futuresInstrumentSuite = _customizationSettings.futuresInstrumentSuite();

		String fixFloatCalibrationMetric = fixFloatInstrumentSuite.calibrationMetric();

		String depositCalibrationMetric = depositInstrumentSuite.calibrationMetric();

		String futuresCalibrationMetric = futuresInstrumentSuite.calibrationMetric();

		SingleStreamComponent[] t1FuturesComponentArray = futuresComponentArray (t1);

		SingleStreamComponent[] t1DepositComponentArray = depositComponentArray (t1);

		FixFloatComponent[] t1FixFloatComponentArray = fixFloatComponentArray (t1);

		double[] t1FixFloatQuoteArray = new double[t1FixFloatComponentArray.length];
		double[] t1FuturesQuoteArray = new double[t1FuturesComponentArray.length];
		double[] t1DepositQuoteArray = new double[t1DepositComponentArray.length];
		ValuationParams valuationParams = null;

		String currency = t0MergedDiscountForwardCurve.currency();

		try {
			valuationParams = new ValuationParams (t1, t1, currency);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			t0MergedDiscountForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		for (int depositComponentIndex = 0;
			depositComponentIndex < t1DepositComponentArray.length;
			++depositComponentIndex)
		{
			try {
				t1DepositQuoteArray[depositComponentIndex] =
					t1DepositComponentArray[depositComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						depositCalibrationMetric
					);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		for (int futuresComponentIndex = 0;
			futuresComponentIndex < t1FuturesComponentArray.length;
			++futuresComponentIndex)
		{
			try {
				t1FuturesQuoteArray[futuresComponentIndex] =
					t1FuturesComponentArray[futuresComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						futuresCalibrationMetric
					);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		for (int fixfloatComponentIndex = 0;
			fixfloatComponentIndex < t1FixFloatComponentArray.length;
			++fixfloatComponentIndex)
		{
			try {
				t1FixFloatQuoteArray[fixfloatComponentIndex] =
					t1FixFloatComponentArray[fixfloatComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						fixFloatCalibrationMetric
					);
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		try {
			return new FundingStateEvaluation (
				ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
					currency,
					_customizationSettings.linearLatentStateCalibrator(),
					new LatentStateStretchSpec[] {
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							depositInstrumentSuite.code(),
							t1DepositComponentArray,
							depositCalibrationMetric,
							t1DepositQuoteArray
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							futuresInstrumentSuite.code(),
							t1FuturesComponentArray,
							futuresCalibrationMetric,
							t1FuturesQuoteArray
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							fixFloatInstrumentSuite.code(),
							t1FixFloatComponentArray,
							fixFloatCalibrationMetric,
							t1FixFloatQuoteArray
						)
					},
					valuationParams,
					null,
					null,
					null,
					1.
				),
				t1DepositComponentArray,
				t1FuturesComponentArray,
				t1FixFloatComponentArray,
				new FundingMarkSuite (
					t1,
					t1DepositQuoteArray,
					t1FuturesQuoteArray,
					t1FixFloatQuoteArray
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Map of T0 EOD and the T1 SOD Funding States
	 * 
	 * @param t0FundingCalibrationSuite T0 Funding Calibration Suite
	 * @param t1 T1
	 * 
	 * @return Map of T0 EOD and the T1 SOD Funding States
	 */

	public Map<String, FundingStateEvaluation> t0EODT1SOD (
		final FundingMarkSuite t0FundingCalibrationSuite,
		final JulianDate t1)
	{
		FundingStateEvaluation t0EODFundingStateEvaluation = t0EOD (t0FundingCalibrationSuite);

		if (null == t0EODFundingStateEvaluation) {
			return null;
		}

		Map<String, FundingStateEvaluation> fundingStateEvaluationMap =
			new CaseInsensitiveHashMap<FundingStateEvaluation>();

		fundingStateEvaluationMap.put ("TOEOD", t0EODFundingStateEvaluation);

		fundingStateEvaluationMap.put (
			"T1SOD",
			t1SOD (t1, t0EODFundingStateEvaluation.mergedDiscountForwardCurve())
		);

		return fundingStateEvaluationMap;
	}
}
