
package org.drip.sample.ois;

import java.util.*;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.numerical.common.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
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
 * <i>IndexFundCurvesReconciliation</i> demonstrates the Construction, Usage, Coupon Extraction and Measure
 *  Generation for an OIS Product Sample using the Index and the Fund Curves, and their Reconciliation.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/ois/README.md">Index/Fund OIS Curve Reconciliation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class IndexFundCurvesReconciliation
{

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityDaysIndex = 0; maturityDaysIndex < maturityDaysArray.length; ++maturityDaysIndex) {
			depositComponentArray[maturityDaysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityDaysIndex], currency),
				OvernightLabel.Create (currency)
			);
		}

		return depositComponentArray;
	}

	private static final FixFloatComponent[] OvernightIndexSwapFromMaturityTenor (
		final JulianDate effectiveDate,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
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

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityTenorIndex],
				"6M"
			) ? maturityTenorArray[maturityTenorIndex] : "6M";

			String floatingTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityTenorIndex],
				"3M"
			) ? maturityTenorArray[maturityTenorIndex] : "3M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							fixedTenor,
							maturityTenorArray[maturityTenorIndex],
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
							couponArray[maturityTenorIndex],
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
							maturityTenorArray[maturityTenorIndex],
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
							OvernightLabel.Create (currency),
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
							0.
						)
					)
				),
				new CashSettleParams (0, currency, 0)
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[maturityTenorIndex] + "." + currency);

			oisArray[maturityTenorIndex] = ois;
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OvernightFundSwapFromMaturityTenor (
		final JulianDate effectiveDate,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
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

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"ON",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
			null,
			OvernightLabel.Create (currency),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting floatingCompositePeriodSetting = new CompositePeriodSetting (
			360,
			"ON",
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			String fixedTenor = Helper.LEFT_TENOR_LESSER == Helper.TenorCompare (
				maturityTenorArray[maturityTenorIndex],
				"6M"
			) ? maturityTenorArray[maturityTenorIndex] : "6M";

			FixFloatComponent ois = new FixFloatComponent (
				new Stream (
					CompositePeriodBuilder.FixedCompositeUnit (
						CompositePeriodBuilder.RegularEdgeDates (
							effectiveDate,
							fixedTenor,
							maturityTenorArray[maturityTenorIndex],
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
							couponArray[maturityTenorIndex],
							0.,
							currency
						)
					)
				),
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.OvernightEdgeDates (
							effectiveDate,
							effectiveDate.addTenor (maturityTenorArray[maturityTenorIndex]),
							null
						),
						floatingCompositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[maturityTenorIndex] + "." + currency);

			oisArray[maturityTenorIndex] = ois;
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OvernightIndexFuturesFromMaturityTenor (
		final JulianDate spotDate,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[startTenorArray.length];

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

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
							OvernightLabel.Create (currency),
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

	private static final FixFloatComponent[] OvernightFundFutureFromMaturityTenor (
		final JulianDate spotDate,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray,
		final String currency)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[startTenorArray.length];

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

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

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"ON",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
			null,
			OvernightLabel.Create (currency),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting floatingCompositePeriodSetting = new CompositePeriodSetting (
			4,
			"ON",
			currency,
			null,
			-1.,
			null,
			null,
			null,
			null
		);

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
						fixedUnitCouponAccrualSetting,
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
						floatingCompositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			ois.setPrimaryCode ("OIS." + maturityTenorArray[tenorIndex] + "." + currency);

			oisArray[tenorIndex] = ois;
		}

		return oisArray;
	}

	private static final MergedDiscountForwardCurve CustomOISCurveBuilderSample (
		final JulianDate today,
		final String headerComment,
		final String currency,
		final boolean overnightIndex)
		throws Exception
	{
		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     " + headerComment);

		System.out.println ("\t----------------------------------------------------------------");

		SingleStreamComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
			today,
			currency,
			new int[]
			{
				1,
				2,
				3
			}
		);

		double[] depositQuoteArray =
		{
			0.0004,
			0.0004,
			0.0004
		};

		double[] shortEndOISQuoteArray =
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

		CalibratableComponent[] shortEndOISComponentArray = overnightIndex ?
			OvernightIndexSwapFromMaturityTenor (
				today,
				new String[]
				{
					"1W",
					"2W",
					"3W",
					"1M"
				},
				shortEndOISQuoteArray,
				currency
			) : OvernightFundSwapFromMaturityTenor (
				today,
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

		double[] oisFuturesQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		CalibratableComponent[] oisFuturesComponentArray = overnightIndex ?
			OvernightIndexFuturesFromMaturityTenor (
				today,
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
				oisFuturesQuoteArray,
				currency
			) :
			OvernightFundFutureFromMaturityTenor (
				today,
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
				oisFuturesQuoteArray,
				currency
			);

		double[] longEndOISQuoteArray =
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

		CalibratableComponent[] longEndOISComponentArray = overnightIndex ?
			OvernightIndexSwapFromMaturityTenor (
				today,
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
			) : OvernightFundSwapFromMaturityTenor (
				today,
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

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		MergedDiscountForwardCurve discountCurve = ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
			currency,
			new LinearLatentStateCalibrator (
				new SegmentCustomBuilderControl (
					MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
					new PolynomialFunctionSetParams (4),
					SegmentInelasticDesignControl.Create (2, 2),
					new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
					null
				),
				BoundarySettings.NaturalStandard(),
				MultiSegmentSequence.CALIBRATE,
				null,
				null
			),
			new LatentStateStretchSpec[]
			{
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"   DEPOSIT   ",
					depositComponentArray,
					"ForwardRate",
					depositQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SHORT END OIS",
					shortEndOISComponentArray,
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					" OIS FUTURE  ",
					oisFuturesComponentArray,
					"SwapRate",
					oisFuturesQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"LONG END OIS ",
					longEndOISComponentArray,
					"SwapRate",
					longEndOISQuoteArray
				)
			},
			valuationParams,
			null,
			null,
			null,
			1.
		);

		System.out.println ("\t----------------------------------------------------------------");

		System.out.println ("\t     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositComponentArray.length; ++depositIndex) {
			System.out.println (
				"\t[" + depositComponentArray[depositIndex].effectiveDate() + " => " +
					depositComponentArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
						depositComponentArray[depositIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
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

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int shortEndOISIndex = 0;
			shortEndOISIndex < shortEndOISComponentArray.length;
			++shortEndOISIndex)
		{
			System.out.println (
				"\t[" + shortEndOISComponentArray[shortEndOISIndex].effectiveDate() + " => " +
					shortEndOISComponentArray[shortEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						shortEndOISComponentArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
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
					) + " | " + FormatUtil.FormatDouble (
						shortEndOISComponentArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
				);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesComponentArray.length; ++oisFuturesIndex) {
			System.out.println (
				"\t[" + oisFuturesComponentArray[oisFuturesIndex].effectiveDate() + " => " +
					oisFuturesComponentArray[oisFuturesIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						oisFuturesComponentArray[oisFuturesIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
							null,
							"SwapRate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						oisFuturesQuoteArray[oisFuturesIndex],
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						oisFuturesComponentArray[oisFuturesIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISComponentArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t[" + longEndOISComponentArray[longEndOISIndex].effectiveDate() + " => " +
					longEndOISComponentArray[longEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						longEndOISComponentArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
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
					) + " | " + FormatUtil.FormatDouble (
						longEndOISComponentArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							MarketParamsBuilder.Create (
								discountCurve,
								null,
								null,
								null,
								null,
								null,
								null
							),
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
			);
		}

		return discountCurve;
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

		String currency = "EUR";

		JulianDate today = DateUtil.CreateFromYMD (2018, DateUtil.FEBRUARY, 18);

		JulianDate customOISStartDate = today.subtractTenor ("2M");

		FixFloatComponent ois = new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						customOISStartDate,
						"6M",
						"4M",
						null
					),
					new CompositePeriodSetting (
						2,
						"6M",
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
						true,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						"6M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						0.003,
						0.,
						currency
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						customOISStartDate,
						"3M",
						"4M",
						null
					),
					new CompositePeriodSetting (
						4,
						"3M",
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
						OvernightLabel.Create (currency),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new CashSettleParams (0, currency, 0)
		);

		CurveSurfaceQuoteContainer mktParamsFund = MarketParamsBuilder.Create (
			CustomOISCurveBuilderSample (
				today,
				"---- DISCOUNT CURVE WITH OVERNIGHT FUND ---",
				currency,
				false
			),
			null,
			null,
			null,
			null,
			null,
			null
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t----------------------------------------------------------------\n");

		Map<String, Double> oisIndexOutputMap = ois.value (
			valuationParams,
			null,
			MarketParamsBuilder.Create (
				CustomOISCurveBuilderSample (
					today,
					"---- DISCOUNT CURVE WITH OVERNIGHT INDEX ---",
					currency,
					true
				),
				null,
				null,
				null,
				null,
				null,
				null
			),
			null
		);

		Map<String, Double> oisFundOutputMap = ois.value (
			valuationParams,
			null,
			mktParamsFund,
			null
		);

		for (Map.Entry<String, Double> mapEntry : oisIndexOutputMap.entrySet()) {
			String key = mapEntry.getKey();

			double indexMeasure = mapEntry.getValue();

			double fundMeasure = oisFundOutputMap.get (key);

			System.out.println ("\t" +
				FormatUtil.FormatDouble (indexMeasure, 1, 8, 1.) + " | " +
				FormatUtil.FormatDouble (fundMeasure, 1, 8, 1.) + " | " + (
					NumberUtil.WithinTolerance (indexMeasure, fundMeasure, 1.e-08, 1.e-04) ?
						"RECONCILES" : "DOES NOT RECONCILE"
				) + " <= " + key
			);
		}

		EnvManager.TerminateEnv();
	}
}
