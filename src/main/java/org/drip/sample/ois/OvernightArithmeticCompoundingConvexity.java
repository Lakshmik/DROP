
package org.drip.sample.ois;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.daycount.Convention;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.*;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
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
 * <i>OvernightArithmeticCompoundingConvexity</i> contains an assessment of the impact of the Overnight Index
 *  Volatility, the Funding Numeraire Volatility, and the ON Index/Funding Correlation on the Overnight
 *  Floating Stream.
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

public class OvernightArithmeticCompoundingConvexity
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

	private static final FixFloatComponent[] OISFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] oisArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			oisArray[maturityTenorIndex] = OTCOISFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);
		}

		return oisArray;
	}

	private static final FixFloatComponent[] OISFuturesFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] startTenorArray,
		final String[] maturityTenorArray,
		final double[] couponIndex)
		throws Exception
	{
		FixFloatComponent[] oisFuturesArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex< maturityTenorArray.length; ++maturityIndex) {
			oisFuturesArray[maturityIndex] = OTCOISFixFloat (
				spotDate.addTenor (startTenorArray[maturityIndex]),
				currency,
				maturityTenorArray[maturityIndex],
				couponIndex[maturityIndex]
			);
		}

		return oisFuturesArray;
	}

	private static final MergedDiscountForwardCurve CustomOISCurveBuilderSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		double[] shortEndOISQuoteArray =
		{
			0.00070,    //   1W
			0.00069,    //   2W
			0.00078,    //   3W
			0.00074     //   1M
		};

		double[] oisFutureQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

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

		return ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
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
					DepositInstrumentsFromMaturityDays (
						spotDate,
						currency,
						new int[]
						{
							1,
							2,
							3
						}
					),
					"ForwardRate",
					new double[] {
						0.0004,
						0.0004,
						0.0004
					}
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"SHORT END OIS",
					OISFromMaturityTenor (
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
					),
					"SwapRate",
					shortEndOISQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					" OIS FUTURE  ",
					OISFuturesFromMaturityTenor (
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
						oisFutureQuoteArray
					),
					"SwapRate",
					oisFutureQuoteArray
				),
				LatentStateStretchBuilder.ForwardFundingStretchSpec (
					"LONG END OIS ",
					OISFromMaturityTenor (
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
					),
					"SwapRate",
					longEndOISQuoteArray
				)
			},
			new ValuationParams (spotDate, spotDate, currency),
			null,
			null,
			null,
			1.
		);
	}

	private static final LatentStateFixingsContainer SetFlatOvernightFixings (
		final JulianDate startDate,
		final JulianDate endDate,
		final JulianDate valuationDate,
		final ForwardLabel forwardLabel,
		final double flatFixing,
		final double notional)
		throws Exception
	{
		LatentStateFixingsContainer latentStateFixingsContainer = new LatentStateFixingsContainer();

		int valuationDateJulian = valuationDate.julian();

		int startDateJulian = startDate.julian();

		JulianDate date = startDate.addDays (1);

		int endDateJulian = endDate.julian();

		int dateJulian = date.julian();

		double account = 1.;
		int previousDateJulian = startDateJulian;

		while (dateJulian <= endDateJulian) {
			latentStateFixingsContainer.add (date, forwardLabel, flatFixing);

			if (dateJulian <= valuationDateJulian) {
				account *= (
					1. + flatFixing * Convention.YearFraction (
						previousDateJulian,
						date.julian(),
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
			"\t|| Manual Calc Float Accrued (Geometric Compounding): " + (account - 1.) * notional
		);

		System.out.println (
			"\t|| Manual Calc Float Accrued (Arithmetic Compounding): " +
				((valuationDateJulian - startDateJulian) * notional * flatFixing / 360.)
		);

		return latentStateFixingsContainer;
	}

	private static final void SetMarketParams (
		final int valuationDateJulian,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final String currency,
		final ForwardLabel forwardLabel,
		final double oisVolatility,
		final double fundingVolatility,
		final double fundingOISCorrelation)
		throws Exception
	{
		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDateJulian,
				VolatilityLabel.Standard (forwardLabel),
				forwardLabel.currency(),
				oisVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDateJulian,
				VolatilityLabel.Standard (fundingLabel),
				currency,
				fundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel,
			fundingLabel,
			new Flat (fundingOISCorrelation)
		);
	}

	private static final void VolatilityCorrelationScenario (
		final Stream[] floatStreamArray,
		final String currency,
		final ForwardLabel forwardLabel,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final double oisVolatility,
		final double fundingVolatility,
		final double fundingOISCorrelation)
		throws Exception
	{
		SetMarketParams (
			valuationParams.valueDate(),
			curveSurfaceQuoteContainer,
			currency,
			forwardLabel,
			oisVolatility,
			fundingVolatility,
			fundingOISCorrelation
		);

		String dump = "\t[" + FormatUtil.FormatDouble (oisVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingOISCorrelation, 2, 0, 100.) + "%] = ";

		for (int floatStreamIndex = 0; floatStreamIndex < floatStreamArray.length; ++floatStreamIndex) {
			Map<String, Double> measureMap = floatStreamArray[floatStreamIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			if (0 != floatStreamIndex) {
				dump += " || ";
			}

			dump += FormatUtil.FormatDouble (
				measureMap.get ("UnadjustedFairPremium"),
				1,
				4,
				100.
			) + "% | " + FormatUtil.FormatDouble (
				measureMap.get ("CompoundingAdjustmentFactor") - 1.,
				1,
				2,
				100.
			) + "% | " + FormatUtil.FormatDouble (
				measureMap.get ("CumulativeConvexityAdjustmentFactor") - 1.,
				1,
				2,
				100.
			) + "%";
		}

		System.out.println (dump);
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

		double[] oisVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] fundingVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] fundingOISCorrelationArray =
		{
			-0.3,
			 0.0,
			 0.3
		};

		JulianDate today = DateUtil.Today().addTenor ("0D");

		JulianDate customOISMaturityDate = today.addTenor ("4M");

		JulianDate customOISStartDate = today.subtractTenor ("2M");

		ForwardLabel forwardLabel = OvernightLabel.Create (currency);

		Stream[] floatStreamArray = {
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						customOISStartDate,
						"6M",
						"6M",
						null
					),
					new CompositePeriodSetting (
						360,
						"ON",
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
			)
		};

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			CustomOISCurveBuilderSample (today, currency),
			null,
			null,
			null,
			null,
			null,
			SetFlatOvernightFixings (
				customOISStartDate,
				customOISMaturityDate,
				today,
				forwardLabel,
				0.003,
				-1.
			)
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		System.out.println (
			"\n\t-------------------------------------------------------------------------------------"
		);

		System.out.println ("\tInput Order (LHS) L->R:");

		System.out.println ("\t\tOIS Volatility, Funding Volatility, OIS/Funding Correlation\n");

		System.out.println ("\tOutput Order (RHS) L->R:");

		System.out.println (
			"\t\tUnadjusted Fair Premium, Compounding Adjustment Factor (% - Relative), Convexity Adjustment Factor (% - Relative)\n"
		);

		System.out.println (
			"\t-------------------------------------------------------------------------------------"
		);

		for (double oisVolatility : oisVolatilityArray) {
			for (double fundingVolatility : fundingVolatilityArray) {
				for (double fundingOISCorrelation : fundingOISCorrelationArray) {
					VolatilityCorrelationScenario (
						floatStreamArray,
						currency,
						forwardLabel,
						valuationParams,
						curveSurfaceQuoteContainer,
						oisVolatility,
						fundingVolatility,
						fundingOISCorrelation
					);
				}
			}
		}

		System.out.println ("\t-------------------------------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
