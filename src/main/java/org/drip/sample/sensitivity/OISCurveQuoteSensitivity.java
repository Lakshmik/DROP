
package org.drip.sample.sensitivity;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.param.creator.*;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.ExponentialTensionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.*;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.*;
import org.drip.state.inference.*;

/*
 * -*- mode: java; tab-width: 4; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 */

/*!
 * Copyright (C) 2030 Lakshmi Krishnamurthy
 * Copyright (C) 2029 Lakshmi Krishnamurthy
 * Copyright (C) 2028 Lakshmi Krishnamurthy
 * Copyright (C) 2027 Lakshmi Krishnamurthy
 * Copyright (C) 2026 Lakshmi Krishnamurthy
 * Copyright (C) 2025 Lakshmi Krishnamurthy
 * Copyright (C) 2024 Lakshmi Krishnamurthy
 * Copyright (C) 2023 Lakshmi Krishnamurthy
 * Copyright (C) 2022 Lakshmi Krishnamurthy
 * Copyright (C) 2021 Lakshmi Krishnamurthy
 * Copyright (C) 2020 Lakshmi Krishnamurthy
 * Copyright (C) 2019 Lakshmi Krishnamurthy
 * Copyright (C) 2018 Lakshmi Krishnamurthy
 * Copyright (C) 2017 Lakshmi Krishnamurthy
 * Copyright (C) 2016 Lakshmi Krishnamurthy
 * Copyright (C) 2015 Lakshmi Krishnamurthy
 * Copyright (C) 2014 Lakshmi Krishnamurthy
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
 * <i>OISCurveQuoteSensitivity</i> demonstrates the calculation of the OIS discount curve sensitivity to the
 * 	calibration instrument quotes. It does the following:
 * 	- Construct the Array of Cash/OIS Instruments and their Quotes from the given set of parameters.
 * 	- Construct the Cash/OIS Instrument Set Stretch Builder.
 * 	- Set up the Linear Curve Calibrator using the following parameters:
 * 		- Cubic Exponential Mixture Basis Spline Set
 * 		- Ck = 2, Segment Curvature Penalty = 2
 * 		- Quadratic Rational Shape Controller
 * 		- Natural Boundary Setting
 * 	- Construct the Shape Preserving OIS Discount Curve by applying the linear curve calibrator to the array
 * 		of Cash and OIS Stretches.
 * 	- Cross-Comparison of the Cash/OIS Calibration Instrument "Rate" metric across the different curve
 * 		construction methodologies.
 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
 * 	- Display of the OIS Instrument Discount Factor Quote Jacobian Sensitivities.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/sensitivity/README.md">Forward Funding OIS Curve Sensitivity</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class OISCurveQuoteSensitivity
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				overnightLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent[] OvernightIndexFromMaturityTenor (
		final JulianDate effectiveDate,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		UnitCouponAccrualSetting fixedUnitCouponAccrualSetting = new UnitCouponAccrualSetting (
			2,
			"Act/360",
			false,
			"Act/360",
			false,
			currency,
			false,
			CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
		);

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityIndex],
				"6M"
			) ? maturityTenorArray[maturityIndex] : "6M";

			String floatingTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityIndex],
				"3M"
			) ? maturityTenorArray[maturityIndex] : "3M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							fixedTenor,
							maturityTenorArray[maturityIndex],
							null
						),
						new CompositePeriodSetting (
							2,
							fixedTenor,
							currency,
							null,
							1.,
							null,
							null,
							null,
							null
						),
						fixedUnitCouponAccrualSetting,
						new ComposableFixedUnitSetting (
							fixedTenor,
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
							null,
							couponArray[maturityIndex],
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							floatingTenor,
							maturityTenorArray[maturityIndex],
							null
						),
						new CompositePeriodSetting (
							4,
							floatingTenor,
							currency,
							null,
							-1.,
							null,
							null,
							null,
							null
						),
						new ComposableFloatingUnitSetting (
							"ON",
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
							null,
							overnightLabel,
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
							0.
						)
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[maturityIndex] + "." + currency);

			oisArray[maturityIndex] = ois;
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OvernightIndexFutureFromMaturityTenor (
		final JulianDate spotDate,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[startTenorArray.length];

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		for (int tenorIndex = 0; tenorIndex < startTenorArray.length; ++tenorIndex) {
			JulianDate effectiveDate = spotDate.addTenor (startTenorArray[tenorIndex]);

			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[tenorIndex],
				"6M"
			) ? maturityTenorArray[tenorIndex] : "6M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"6M",
							maturityTenorArray[tenorIndex],
							null
						),
						new CompositePeriodSetting (
							2,
							fixedTenor,
							currency,
							null,
							1.,
							null,
							null,
							null,
							null
						),
						new UnitCouponAccrualSetting (
							2,
							"Act/360",
							false,
							"Act/360",
							false,
							currency,
							false,
							CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
						),
						new ComposableFixedUnitSetting (
							fixedTenor,
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
							null,
							couponArray[tenorIndex],
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							"3M",
							maturityTenorArray[tenorIndex],
							null
						),
						new CompositePeriodSetting (
							4,
							Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
								maturityTenorArray[tenorIndex],
								"3M"
							) ? maturityTenorArray[tenorIndex] : "3M",
							currency,
							null,
							-1.,
							null,
							null,
							null,
							null
						),
						new ComposableFloatingUnitSetting (
							"ON",
							CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
							null,
							overnightLabel,
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
							0.
						)
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[tenorIndex] + "." + currency);

			oisArray[tenorIndex] = ois;
		}

		return oisArray;
	}

	/*
	 * This sample demonstrates the calculation of the discount curve sensitivity to the calibration
	 * 	instrument quotes. It does the following:
	 * 	- Construct the Array of Cash/OIS Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Cash/OIS Instrument Set Stretch Builder.
	 * 	- Set up the Linear Curve Calibrator using the following parameters:
	 * 		- Cubic Exponential Mixture Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
	 * 		of Cash and OIS Stretches.
	 * 	- Cross-Comparison of the Cash/OIS Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Display of the Cash Instrument Discount Factor Quote Jacobian Sensitivities.
	 * 	- Display of the OIS Instrument Discount Factor Quote Jacobian Sensitivities.
	 */

	private static final void OISCurveQuoteSensitivitySample (
		final JulianDate spotDate,
		final String headerComment,
		final String currency)
		throws Exception
	{
		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t|| " + headerComment);

		SingleStreamComponent[] depositArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[]
			{
				1,
				2,
				3
			}
		);

		double[] depositQuoteArray = new double[]
		{
			0.0004,
			0.0004,
			0.0004		 // Deposit
		};

		LatentStateStretchSpec depositStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"   DEPOSIT   ",
			depositArray,
			"ForwardRate",
			depositQuoteArray
		);

		double[] shortEndOISQuoteArray = new double[]
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

		FixFloatComponent[] shortEndOISArray = OvernightIndexFromMaturityTenor (
			spotDate,
			new String[]
			{
				"1W",
				"2W",
				"3W",
				"1M"
			},
			shortEndOISQuoteArray,
			currency
		);

		LatentStateStretchSpec oisShortEndStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"SHORT END OIS",
			shortEndOISArray,
			"SwapRate",
			shortEndOISQuoteArray
		);

		double[] oisFutureQuoteArray = new double[]
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		FixFloatComponent[] oisFuturesArray = OvernightIndexFutureFromMaturityTenor (
			spotDate,
			new String[]
			{
				"1M",
				"2M",
				"3M",
				"4M",
				"5M"
			},
			new String[]
			{
				"1M",
				"1M",
				"1M",
				"1M",
				"1M"
			},
			oisFutureQuoteArray,
			currency
		);

		LatentStateStretchSpec oisFutureStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			" OIS FUTURE  ",
			oisFuturesArray,
			"SwapRate",
			oisFutureQuoteArray
		);

		double[] longEndOISQuoteArray = new double[]
		{
			0.00002,    //  15M
			0.00008,    //  18M
			0.00021,    //  21M
			0.00036,    //   2Y
			0.00127,    //   3Y
			0.00274,    //   4Y
			0.00456,    //   5Y
			0.00647,    //   6Y
			0.00827,    //   7Y
			0.00996,    //   8Y
			0.01147,    //   9Y
			0.01280,    //  10Y
			0.01404,    //  11Y
			0.01516,    //  12Y
			0.01764,    //  15Y
			0.01939,    //  20Y
			0.02003,    //  25Y
			0.02038     //  30Y
		};

		FixFloatComponent[] longEndOISArray = OvernightIndexFromMaturityTenor (
			spotDate,
			new String[]
			{
				"15M",
				"18M",
				"21M",
				 "2Y",
				 "3Y",
				 "4Y",
				 "5Y",
				 "6Y",
				 "7Y",
				 "8Y",
				 "9Y",
				"10Y",
				"11Y",
				"12Y",
				"15Y",
				"20Y",
				"25Y",
				"30Y"
			},
			longEndOISQuoteArray,
			currency
		);

		LatentStateStretchSpec oisLongEndStretch = LatentStateStretchBuilder.ForwardFundingStretchSpec (
			"LONG END OIS ",
			longEndOISArray,
			"SwapRate",
			longEndOISQuoteArray
		);

		LinearLatentStateCalibrator linearLatentStateCalibrator = new LinearLatentStateCalibrator (
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			),
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			depositStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			)
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			oisShortEndStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			)
		);

		linearLatentStateCalibrator.setStretchSegmentBuilderControl (
			oisLongEndStretch.name(),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
				new ExponentialTensionSetParams (2.),
				SegmentInelasticDesignControl.Create (2, 2),
				new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
				new PreceedingManifestSensitivityControl (true, 1, null)
			)
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			linearLatentStateCalibrator,
			new LatentStateStretchSpec[]
			{
				depositStretch,
				oisShortEndStretch,
				oisFutureStretch,
				oisLongEndStretch
			},
			valuationParams,
			null,
			null,
			null,
			1.
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] => " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
						MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SHORT END OIS INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int shortEndOISIndex = 0; shortEndOISIndex < shortEndOISArray.length; ++shortEndOISIndex) {
			System.out.println (
				"\t|| [" + shortEndOISArray[shortEndOISIndex].maturityDate() + "] => " +
					FormatUtil.FormatDouble (
						shortEndOISArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
							null,
							"CalibSwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						shortEndOISQuoteArray[shortEndOISIndex],
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesArray.length; ++oisFuturesIndex) {
			System.out.println (
				"\t|| [" + oisFuturesArray[oisFuturesIndex].maturityDate() + "] => " +
				FormatUtil.FormatDouble (
					oisFuturesArray[oisFuturesIndex].measureValue (
						valuationParams,
						null,
						MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
						null,
						"SwapRate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					oisFutureQuoteArray[oisFuturesIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     LONG END OIS INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t|| [" + longEndOISArray[longEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						longEndOISArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
							null,
							"CalibSwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						longEndOISQuoteArray[longEndOISIndex],
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| " + depositArray[depositIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						depositArray[depositIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SHORT END OIS MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int shortEndOISIndex = 0; shortEndOISIndex < shortEndOISArray.length; ++shortEndOISIndex) {
			System.out.println (
				"\t|| " + shortEndOISArray[shortEndOISIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						shortEndOISArray[shortEndOISIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS FUTURE MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesArray.length; ++oisFuturesIndex) {
			System.out.println (
				"\t|| " + oisFuturesArray[oisFuturesIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						oisFuturesArray[oisFuturesIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     LONG END OIS MATURITY DISCOUNT FACTOR JACOBIAN");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t|| " + longEndOISArray[longEndOISIndex].maturityDate() + " => " +
					discountCurve.jackDDFDManifestMeasure (
						longEndOISArray[longEndOISIndex].maturityDate(),
						"PV"
					).displayString()
			);
		}
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 * 
	 * @throws Exception Thrown on Error/Exception Situation
	 */

	public static final void main (
		final String[] argumentArray)
		throws Exception
	{
		EnvManager.InitEnv ("");

		JulianDate spotDate = DateUtil.CreateFromYMD (2018, DateUtil.MARCH, 8);

		OISCurveQuoteSensitivitySample (spotDate, "---- DISCOUNT CURVE WITH OVERNIGHT INDEX ---", "EUR");

		EnvManager.TerminateEnv();
	}
}
