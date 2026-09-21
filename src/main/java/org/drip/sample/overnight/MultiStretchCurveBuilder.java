
package org.drip.sample.overnight;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
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
 * <i>MultiStretchCurveBuilder</i> contains a sample of the construction and usage of the Overnight Curve
 * 	built using the Overnight Indexed Swap Product Instruments in their distinct stretches. The Tenors/Quotes
 * 	to replicate are taken from:
 * 
 * <br><br>
 * 	<ul>
 * 		<li>
 * 			Ametrano, F., and M. Bianchetti (2013): Everything You Always Wanted to Know About Multiple
 * 				Interest Rate Curve Bootstrapping but Were Afraid to Ask
 * 				http://papers.ssrn.com/sol3/papers.cfm?abstract_id=2219548
 * 		</li>
 * 	</ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/overnight/README.md">Shape Preserving Stretch Overnight Curve</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class MultiStretchCurveBuilder
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

	private static final void CustomOISCurveBuilderSample (
		final JulianDate spotDate,
		final String currency,
		final String headerComment)
		throws Exception
	{
		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     " + headerComment);

		System.out.println ("\t||----------------------------------------------------------------");

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

		CalibratableComponent[] shortEndOISArray = OISFromMaturityTenor (
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

		double[] oisFutureQuoteArray =
		{
			 0.00046,    //   1M x 1M
			 0.00016,    //   2M x 1M
			-0.00007,    //   3M x 1M
			-0.00013,    //   4M x 1M
			-0.00014     //   5M x 1M
		};

		CalibratableComponent[] oisFuturesArray = OISFuturesFromMaturityTenor (
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

		CalibratableComponent[] longEndOISArray = OISFromMaturityTenor (
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

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer =
			MarketParamsBuilder.Create (
				ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
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
							"DEPOSIT",
							depositArray,
							new String[]
							{
								"ForwardRate",		// 1D
								"ForwardRate",		// 2D
								"ForwardRate"		// 3D
							},
							depositQuoteArray
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							"OIS_SHORT_END",
							shortEndOISArray,
							new String[]
							{
								"SwapRate",    //   1W
								"SwapRate",    //   2W
								"SwapRate",    //   3W
								"SwapRate"     //   1M
							},
							shortEndOISQuoteArray
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							"OIS_FUTURE",
							oisFuturesArray,
							new String[]
							{
								"SwapRate",    //   1M
								"SwapRate",    //   2M
								"SwapRate",    //   3M
								"SwapRate",    //   4M
								"SwapRate"     //   5M
							},
							oisFutureQuoteArray
						),
						LatentStateStretchBuilder.ForwardFundingStretchSpec (
							"OIS_LONG_END",
							longEndOISArray,
							new String[]
							{
								"SwapRate",    //  15M
								"SwapRate",    //  18M
								"SwapRate",    //  21M
								"SwapRate",    //   2Y
								"SwapRate",    //   3Y
								"SwapRate",    //   4Y
								"SwapRate",    //   5Y
								"SwapRate",    //   6Y
								"SwapRate",    //   7Y
								"SwapRate",    //   8Y
								"SwapRate",    //   9Y
								"SwapRate",    //  10Y
								"SwapRate",    //  11Y
								"SwapRate",    //  12Y
								"SwapRate",    //  15Y
								"SwapRate",    //  20Y
								"SwapRate",    //  25Y
								"SwapRate"     //  30Y
							},
							longEndOISQuoteArray
						)
					},
					valuationParams,
					null,
					null,
					null,
					1.
				),
				null,
				null,
				null,
				null,
				null,
				null
			);

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].effectiveDate() + " => " +
					depositArray[depositIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
						depositArray[depositIndex].measureValue (
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

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS SHORT END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int shortEndOISIndex = 0; shortEndOISIndex < shortEndOISArray.length; ++shortEndOISIndex) {
			System.out.println (
				"\t|| [" + shortEndOISArray[shortEndOISIndex].effectiveDate() + " => " +
					shortEndOISArray[shortEndOISIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
						shortEndOISArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
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
						shortEndOISArray[shortEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"FairPremium"
						),
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
				"\t|| [" + oisFuturesArray[oisFuturesIndex].effectiveDate() + " => " +
					oisFuturesArray[oisFuturesIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
						oisFuturesArray[oisFuturesIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
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
					) + " | " + FormatUtil.FormatDouble (
						oisFuturesArray[oisFuturesIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     OIS LONG END INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int longEndOISIndex = 0; longEndOISIndex < longEndOISArray.length; ++longEndOISIndex) {
			System.out.println (
				"\t|| [" + longEndOISArray[longEndOISIndex].effectiveDate() + " => " +
					longEndOISArray[longEndOISIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
						longEndOISArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
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
						longEndOISArray[longEndOISIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"FairPremium"
						),
						1,
						6,
						1.
					)
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

		String currency = "EUR";

		JulianDate today = DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11);

		CustomOISCurveBuilderSample (today, currency, "OVERNIGHT INDEX RUN RECONCILIATION");

		EnvManager.TerminateEnv();
	}
}
