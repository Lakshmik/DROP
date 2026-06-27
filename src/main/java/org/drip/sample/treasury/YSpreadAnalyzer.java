
package org.drip.sample.treasury;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.definition.Bond;
import org.drip.product.definition.Component;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.ExchangeInstrumentBuilder;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.service.template.OTCInstrumentBuilder;
import org.drip.service.template.TreasuryBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.govvie.ExplicitBootGovvieCurve;
import org.drip.state.nonlinear.FlatForwardGovvieCurve;
import org.drip.state.nonlinear.NonlinearCurveBuilder;

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
 * <i>YSpreadAnalyzer</i> analyzes Y Curve construction and analysis. The Reference is:
 *  
 * 	<br><br>
 *  <ul>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/treasury/README.md">G20 Govvie Bond Definitions YAS</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class YSpreadAnalyzer
{

	private static final MergedDiscountForwardCurve FundingCurve (
		final JulianDate spotDate,
		final String currency,
		final String[] depositMaturityTenorArray,
		final double[] depositQuoteArray,
		final double[] futuresQuoteArray,
		final String[] fixFloatMaturityTenorArray,
		final double[] fixFloatQuoteArray)
		throws Exception
	{
		MergedDiscountForwardCurve fundingCurve = LatentMarketStateBuilder.SmoothFundingCurve (
			spotDate,
			currency,
			depositMaturityTenorArray,
			depositQuoteArray,
			"ForwardRate",
			futuresQuoteArray,
			"ForwardRate",
			fixFloatMaturityTenorArray,
			fixFloatQuoteArray,
			"SwapRate"
		);

		Component[] depositComponentArray = OTCInstrumentBuilder.FundingDeposit (
			spotDate,
			currency,
			depositMaturityTenorArray
		);

		Component[] futuresComponentArray = ExchangeInstrumentBuilder.ForwardRateFuturesPack (
			spotDate,
			futuresQuoteArray.length,
			currency
		);

		Component[] fixFloatComponentArray = OTCInstrumentBuilder.FixFloatStandard (
			spotDate,
			currency,
			"ALL",
			fixFloatMaturityTenorArray,
			"MAIN",
			0.
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			fundingCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\n\t|-------------------------------------||");

		System.out.println ("\t|        DEPOSIT INPUT vs. CALC       ||");

		System.out.println ("\t|-------------------------------------||");

		for (int depositComponentIndex = 0;
			depositComponentIndex < depositComponentArray.length;
			++depositComponentIndex)
		{
			System.out.println (
				"\t| [" + depositComponentArray[depositComponentIndex].maturityDate() + "] =" +
				FormatUtil.FormatDouble (
					depositComponentArray[depositComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"ForwardRate"
					),
					1,
					6,
					1.
				) + " |" + FormatUtil.FormatDouble (
					depositQuoteArray[depositComponentIndex],
					1,
					6,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|-------------------------------------||");

		System.out.println ("\n\t|-------------------------------------||");

		System.out.println ("\t|        FUTURES INPUT vs. CALC       ||");

		System.out.println ("\t|-------------------------------------||");

		for (int futuresComponentIndex = 0;
			futuresComponentIndex < futuresComponentArray.length;
			++futuresComponentIndex)
		{
			System.out.println (
				"\t| [" + futuresComponentArray[futuresComponentIndex].maturityDate() + "] =" +
				FormatUtil.FormatDouble (
					futuresComponentArray[futuresComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"ForwardRate"
					),
					1,
					6,
					1.
				) + " |" + FormatUtil.FormatDouble (
					futuresQuoteArray[futuresComponentIndex],
					1,
					6,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|-------------------------------------||");

		System.out.println ("\n\t|------------------------------------------------|| ");

		System.out.println ("\t|          FIX-FLOAT INPUTS vs CALIB             ||");

		System.out.println ("\t|------------------------------------------------|| ");

		for (int fixFloatComponentIndex = 0;
			fixFloatComponentIndex < fixFloatComponentArray.length;
			++fixFloatComponentIndex)
		{
			System.out.println (
				"\t| [" + fixFloatComponentArray[fixFloatComponentIndex].maturityDate() + "] =" +
				FormatUtil.FormatDouble (
					fixFloatComponentArray[fixFloatComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + " |" + FormatUtil.FormatDouble (
					fixFloatQuoteArray[fixFloatComponentIndex],
					1,
					6,
					1.
				) + " |" + FormatUtil.FormatDouble (
					fixFloatComponentArray[fixFloatComponentIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"FairPremium"
					),
					1,
					6,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|------------------------------------------------||\n");

		return fundingCurve;
	}

	private static final ExplicitBootGovvieCurve YCurve (
		final JulianDate spotDate,
		final String code,
		final Bond[] bondArray,
		final double[] marketYieldArray)
		throws Exception
	{
		int[] dateArray = new int[bondArray.length];
		double[] forwardYieldArray = new double[bondArray.length];

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dateArray[bondIndex] = bondArray[bondIndex].maturityDate().julian();

			forwardYieldArray[bondIndex] = 0.;
		}

		ExplicitBootGovvieCurve explicitBootGovvieCurve = new FlatForwardGovvieCurve (
			spotDate.julian(),
			code,
			bondArray[0].currency(),
			dateArray,
			forwardYieldArray
		);

		return NonlinearCurveBuilder.YieldCurve (
			ValuationParams.Spot (spotDate.julian()),
			bondArray,
			marketYieldArray,
			false,
			explicitBootGovvieCurve
		) ? explicitBootGovvieCurve : null;
	}

	private static final void DumpAssetSwapSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Asset Swap Spread (bp) => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].aswFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpBondBasis (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Bond Basis (bp)        => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].bondBasisFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				100.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpCleanPrice (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Clean Price            => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].priceFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				100.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpConvexity (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Convexity              => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].convexityFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				1.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpDiscountMargin (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Discount Margin (bp)   => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].discountMarginFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpDuration (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Duration               => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].durationFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpESpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| E-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].eSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpGSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| G-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].gSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpISpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| I-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].iSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpJSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| J-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].jSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpMacaulayDuration (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Macaulay Duration      => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].macaulayDurationFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				1.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpModifiedDuration (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Modified Duration      => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].modifiedDurationFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpNSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| N-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].nSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpOAS (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| OAS (bp)               => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].oasFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpTSYSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Treasury Spread (bp)   => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].tsySpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpYield (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Yield (%)              => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].yieldFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				100.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpYield01 (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Yield01 (bp)           => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].yield01FromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
		}

		System.out.println (dump);
	}

	private static final void DumpZSpread (
		final Bond[] bondArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer)
		throws Exception
	{
		String dump = "\t| Z-Spread (bp)          => ";

		for (int bondIndex = 0; bondIndex < bondArray.length; ++bondIndex) {
			dump += FormatUtil.FormatDouble (
				bondArray[bondIndex].zSpreadFromYSpread (
					valuationParams,
					curveSurfaceQuoteContainer,
					null,
					0.
				),
				4,
				6,
				10000.
			) + " |";
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

		JulianDate spotDate = DateUtil.CreateFromYMD (2015, DateUtil.NOVEMBER, 18);

		JulianDate[] effectiveDateArray = new JulianDate[] {
			spotDate,
			spotDate,
			spotDate
		};

		JulianDate[] maturityDateArray = new JulianDate[] {
			spotDate.addTenor ("1Y"),
			spotDate.addTenor ("2Y"),
			spotDate.addTenor ("3Y")
		};

		double[] couponArray = new double[] {
			0.0525,
			0.0525,
			0.0525
		};

		double[] marketYieldArray = new double[] {
			0.035,
			0.040,
			0.045
		};

		String code = "UST";

		String[] depositMaturityTenorArray = new String[] {
			"2D"
		};

		double[] depositQuoteArray = new double[] {
			0.0111956 // 2D
		};

		double[] futuresQuoteArray = new double[] {
			0.011375,	// 98.8625
			0.013350,	// 98.6650
			0.014800,	// 98.5200
			0.016450,	// 98.3550
			0.017850,	// 98.2150
			0.019300	// 98.0700
		};

		String[] fixFloatMaturityTenorArray = new String[] {
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"06Y",
			"07Y",
			"08Y",
			"09Y",
			"10Y",
			"11Y",
			"12Y",
			"15Y",
			"20Y",
			"25Y",
			"30Y",
			"40Y",
			"50Y"
		};

		double[] fixFloatQuoteArray = new double[] {
			0.017029, //  2Y
			0.019354, //  3Y
			0.021044, //  4Y
			0.022291, //  5Y
			0.023240, //  6Y
			0.024025, //  7Y
			0.024683, //  8Y
			0.025243, //  9Y
			0.025720, // 10Y
			0.026130, // 11Y
			0.026495, // 12Y
			0.027230, // 15Y
			0.027855, // 20Y
			0.028025, // 25Y
			0.028028, // 30Y
			0.027902, // 40Y
			0.027655  // 50Y
		};

		System.out.println ("\n\t|-----------------------------------------------------------------------|");

		System.out.println ("\t|                          CALIBRATION SUITE                            |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		System.out.println ("\t|  Inputs => L -> R:                                                    |");

		System.out.println ("\t|    CODE {EFFECTIVE DATE -> MATURITY DATE} COUPON % | MARKET YIELD %   |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		for (int maturityIndex = 0; maturityIndex < maturityDateArray.length; ++maturityIndex) {
			System.out.println (
				"\t| " + code + " {" + effectiveDateArray[maturityIndex] + " -> " +
					maturityDateArray[maturityIndex] + "}" +
					FormatUtil.FormatDouble (couponArray[maturityIndex], 1, 2, 100.) + "% |" +
					FormatUtil.FormatDouble (marketYieldArray[maturityIndex], 1, 2, 100.) + "%"
			);
		}

		System.out.println ("\t|-----------------------------------------------------------------------|");

		Bond[] bondArray = TreasuryBuilder.FromCode (
			code,
			effectiveDateArray,
			maturityDateArray,
			couponArray
		);

		ExplicitBootGovvieCurve explicitBootGovvieCurve = YCurve (
			spotDate,
			code,
			bondArray,
			marketYieldArray
		);

		MergedDiscountForwardCurve fundingCurve = FundingCurve (
			spotDate,
			bondArray[0].currency(),
			depositMaturityTenorArray,
			depositQuoteArray,
			futuresQuoteArray,
			fixFloatMaturityTenorArray,
			fixFloatQuoteArray
		);

		ValuationParams valuationParams = ValuationParams.Spot (spotDate.julian());

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setGovvieState (explicitBootGovvieCurve);

		curveSurfaceQuoteContainer.setFundingState (fundingCurve);

		System.out.println ("\n\t|-----------------------------------------------------------------------|");

		System.out.println ("\t|                               OUTPUTS                                 |");

		System.out.println ("\t|-----------------------------------------------------------------------|");

		DumpAssetSwapSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpBondBasis (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpCleanPrice (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpConvexity (bondArray, valuationParams, curveSurfaceQuoteContainer);

		// DumpCreditBasis (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpDiscountMargin (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpDuration (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpESpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpGSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpISpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpJSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpMacaulayDuration (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpModifiedDuration (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpNSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpOAS (bondArray, valuationParams, curveSurfaceQuoteContainer);

		// DumpPECS (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpTSYSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpYield (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpYield01 (bondArray, valuationParams, curveSurfaceQuoteContainer);

		DumpZSpread (bondArray, valuationParams, curveSurfaceQuoteContainer);

		System.out.println ("\t|-----------------------------------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
