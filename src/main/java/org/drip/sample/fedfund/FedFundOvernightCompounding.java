
package org.drip.sample.fedfund;

import java.util.*;

import org.drip.analytics.cashflow.CompositePeriod;
import org.drip.analytics.date.*;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.definition.OvernightIndex;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.*;
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
import org.drip.state.creator.*;
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
 * <i>FedFundOvernightCompounding</i> demonstrates in detail the methodology behind the overnight compounding
 * 	used in the Overnight fund Floating Stream Accrual.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/fedfund/README.md">Overnight/Composite Fed Fund LIBOR</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FedFundOvernightCompounding
{

	private static final FixFloatComponent OTCOISFixFloat (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return OvernightFixedFloatContainer.FundConventionFromJurisdiction (
			currency
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				overnightLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			oisArray[maturityIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityIndex],
				couponArray[maturityIndex]
			);
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OISFuturesFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisFuturesArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			oisFuturesArray[maturityIndex] = OTCOISFixFloat (
				spotDate.addTenor (startTenorArray[maturityIndex]),
				currency,
				maturityTenorArray[maturityIndex],
				couponArray[maturityIndex]
			);
		}

		return oisFuturesArray;
	}

	private static final MergedDiscountForwardCurve CustomOISCurveBuilderSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		System.out.println ("\t----------------------------------------------------------------");

		SingleStreamComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
			spotDate,
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
			0.0004		 // Deposit
		};

		double[] shortEndOISQuoteArray =
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

		CalibratableComponent[] shortEndOISComponentArray = OISFromMaturityTenor (
			spotDate,
			currency,
			new String[]
			{
				"1W",
				"2W",
				"3W",
				"1M"
			},
			shortEndOISQuoteArray
		);

		double[] oisFuturesQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		CalibratableComponent[] oisFuturesComponentArray = OISFuturesFromMaturityTenor (
			spotDate,
			currency,
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
			oisFuturesQuoteArray
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

		CalibratableComponent[] longEndOISComponentArray = OISFromMaturityTenor (
			spotDate,
			currency,
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
			longEndOISQuoteArray
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

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

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			null,
			null,
			null,
			null,
			null,
			null
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
							curveSurfaceQuoteContainer,
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
			Map<String, Double> shortEndOISMeasureMap = shortEndOISComponentArray[shortEndOISIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double calibrationSwapRate = shortEndOISMeasureMap.get ("CalibSwapRate");

			double fairPremium = shortEndOISMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + shortEndOISComponentArray[shortEndOISIndex].effectiveDate() + " => " +
					shortEndOISComponentArray[shortEndOISIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (calibrationSwapRate, 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (shortEndOISQuoteArray[shortEndOISIndex], 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
					FormatUtil.FormatDouble (fairPremium - calibrationSwapRate, 1, 2, 10000.)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS FUTURE INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int oisFuturesIndex = 0; oisFuturesIndex < oisFuturesComponentArray.length; ++oisFuturesIndex) {
			Map<String, Double> oisFuturesMeasureMap = oisFuturesComponentArray[oisFuturesIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double swapRate = oisFuturesMeasureMap.get ("SwapRate");

			double fairPremium = oisFuturesMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + oisFuturesComponentArray[oisFuturesIndex].effectiveDate() + " => " +
					oisFuturesComponentArray[oisFuturesIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (swapRate, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (oisFuturesQuoteArray[oisFuturesIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium - swapRate, 1, 2, 10000.)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISComponentArray.length; ++longEndOISIndex) {
			Map<String, Double> longEndOISMeasureMap = longEndOISComponentArray[longEndOISIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double calibrationSwapRate = longEndOISMeasureMap.get ("CalibSwapRate");

			double fairPremium = longEndOISMeasureMap.get ("FairPremium");

			System.out.println (
				"\t[" + longEndOISComponentArray[longEndOISIndex].effectiveDate() + " => " +
					longEndOISComponentArray[longEndOISIndex].maturityDate() + "] = " +
				FormatUtil.FormatDouble (calibrationSwapRate, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (longEndOISQuoteArray[longEndOISIndex], 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium, 1, 6, 1.) + " | " +
				FormatUtil.FormatDouble (fairPremium - calibrationSwapRate, 1, 2, 10000.)
			);
		}

		return discountCurve;
	}

	private static final LatentStateFixingsContainer SetFlatOvernightFixings (
		final JulianDate startDate,
		final JulianDate endDate,
		final JulianDate valueDate,
		final ForwardLabel forwardLabel,
		final double flatFixing,
		final double notional)
		throws Exception
	{
		LatentStateFixingsContainer latentStateFixingsContainer = new LatentStateFixingsContainer();

		latentStateFixingsContainer.add (startDate, forwardLabel, flatFixing);

		int previousDateJulian = startDate.julian();

		int valueDateJulian = valueDate.julian();

		JulianDate date = startDate.addDays (1);

		int endDateJulian = endDate.julian();

		int dateJulian = date.julian();

		double account = 1.;

		while (dateJulian <= endDateJulian) {
			latentStateFixingsContainer.add (date, forwardLabel, flatFixing);

			if (dateJulian <= valueDateJulian) {
				account *= (
					1. + flatFixing * Convention.YearFraction (
						previousDateJulian,
						dateJulian,
						"Act/360",
						false,
						null,
						"USD"
					)
				);
			}

			previousDateJulian = dateJulian;

			dateJulian = (date = date.addBusDays (1, "USD")).julian();
		}

		System.out.println (
			"\tManual Calc Float Accrued (Geometric Compounding): " + (account - 1.) * notional
		);

		double dcf = (valueDate.julian() - startDate.julian()) / 360.;

		System.out.println (
			"\tManual Calc Float Accrued (Arithmetic Compounding): " + (dcf * notional * flatFixing)
		);

		System.out.println ("\tManual Calc Float Accrued DCF (Arithmetic Compounding): " + dcf);

		return latentStateFixingsContainer;
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

		String currency = "USD";
		double oisVolatility = 0.3;
		double usdFundingVolatility = 0.3;
		double usdFundingUSDOISCorrelation = 0.3;

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		OvernightLabel overnightLabel = OvernightLabel.Create (currency);

		JulianDate today = DateUtil.CreateFromYMD (2015, DateUtil.JANUARY, 5);

		JulianDate customOISStartDate = today.subtractTenor ("2M");

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

		List<Integer> floatingStreamEdgeDateList =
			CompositePeriodBuilder.RegularEdgeDates (customOISStartDate, "6M", "6M", null);

		List<CompositePeriod> arithmeticFloatPeriodList = CompositePeriodBuilder.FloatingCompositeUnit (
			floatingStreamEdgeDateList,
			floatingCompositePeriodSetting,
			new ComposableFloatingUnitSetting (
				"ON",
				CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
				null,
				overnightLabel,
				CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
				0.
			)
		);

		Stream arithmeticFloatingStream = new Stream (arithmeticFloatPeriodList);

		Stream fixedStream = new Stream (
			CompositePeriodBuilder.FixedCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					customOISStartDate,
					"6M",
					"6M",
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
					360,
					"Act/360",
					false,
					"Act/360",
					false,
					currency,
					false,
					CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
				),
				new ComposableFixedUnitSetting (
					"6M",
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
					null,
					0.,
					0.,
					currency
				)
			)
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			CustomOISCurveBuilderSample (today, currency),
			null,
			null,
			null,
			null,
			null,
			SetFlatOvernightFixings (
				customOISStartDate,
				today.addTenor ("4M"),
				today,
				overnightLabel,
				0.003,
				-1.
			)
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		Map<String, Double> geometricOISMeasureMap = new FixFloatComponent (
			fixedStream,
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					floatingStreamEdgeDateList,
					floatingCompositePeriodSetting,
					new ComposableFloatingUnitSetting (
						"ON",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_OVERNIGHT,
						null,
						ForwardLabel.Create (
							new OvernightIndex (
								currency + "FedFund",
								"FedFund",
								currency,
								"Act/360",
								currency,
								"ON",
								0,
								CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
							),
							"ON"
						),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			cashSettleParams
		).value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		System.out.println (
			"\tMachine Calc Float Accrued (Geometric Compounding): " +
				geometricOISMeasureMap.get ("FloatAccrued")
		);

		System.out.println (
			"\tMachine Calc Float Accrued (Arithmetic Compounding): " +
				new FixFloatComponent (
					fixedStream,
					arithmeticFloatingStream,
					cashSettleParams
				).value (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null
				).get (
					"FloatAccrued"
				)
		);

		System.out.println (
			"\tMachine Calc Float Accrued DCF (Arithmetic Compounding): " +
				Math.abs (
					geometricOISMeasureMap.get ("FloatAccrued") / geometricOISMeasureMap.get ("ResetRate")
				)
		);

		CompositePeriod leadingArithmeticFloatPeriod = arithmeticFloatPeriodList.get (0);

		System.out.println (
			"\tPeriod #1 Coupon Without Convexity Adjustment: " + arithmeticFloatingStream.coupon (
				leadingArithmeticFloatPeriod.endDate(),
				valuationParams,
				curveSurfaceQuoteContainer
			).rate()
		);

		int valueDate = valuationParams.valueDate();

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (overnightLabel),
				currency,
				oisVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (fundingLabel),
				currency,
				usdFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			overnightLabel,
			fundingLabel,
			new Flat (usdFundingUSDOISCorrelation)
		);

		System.out.println (
			"\tPeriod #1 Coupon With Convexity Adjustment: " + arithmeticFloatingStream.coupon (
				leadingArithmeticFloatPeriod.endDate(),
				valuationParams,
				curveSurfaceQuoteContainer
			).rate()
		);

		EnvManager.TerminateEnv();
	}
}
