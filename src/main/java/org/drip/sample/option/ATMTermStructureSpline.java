
package org.drip.sample.option;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.NodeStructure;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.valuation.*;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.option.EuropeanCallPut;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>ATMTermStructureSpline</i> contains an illustration of the Calibration and Extraction of the
 * 	Deterministic ATM Price and Volatility Term Structures using Custom Splines. This does not deal with
 * 	Local Volatility Surfaces.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/option/README.md">Deterministic (Black) / Stochastic (Heston) Options</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class ATMTermStructureSpline
{

	private static final FixFloatComponent OTCIRS (
		final JulianDate spotDate,
		final String currency,
		final String maturityTenor,
		final double coupon)
	{
		return IBORFixedFloatContainer.ConventionFromJurisdiction (
			currency,
			"ALL",
			maturityTenor,
			"MAIN"
		).createFixFloatComponent (
			spotDate,
			maturityTenor,
			coupon,
			0.,
			1.
		);
	}

	private static final CalibratableComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final int[] depositMaturityDaysArray,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibratableComponentArray =
			new CalibratableComponent[depositMaturityDaysArray.length + futuresCount];

		for (int depositIndex = 0; depositIndex < depositMaturityDaysArray.length; ++depositIndex) {
			calibratableComponentArray[depositIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (depositMaturityDaysArray[depositIndex], currency),
				ForwardLabel.Create (currency, "3M")
			);
		}

		CalibratableComponent[] futuresComponentArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int futuresIndex = depositMaturityDaysArray.length;
			futuresIndex < depositMaturityDaysArray.length + futuresCount;
			++futuresIndex)
		{
			calibratableComponentArray[futuresIndex] =
				futuresComponentArray[futuresIndex - depositMaturityDaysArray.length];
		}

		return calibratableComponentArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			FixFloatComponent irs = OTCIRS (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);

			irs.setPrimaryCode ("IRS." + maturityTenorArray[maturityTenorIndex] + "." + currency);

			irsArray[maturityTenorIndex] = irs;
		}

		return irsArray;
	}

	private static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		double[] swapQuoteArray =
		{
			0.02604,    //  4Y
			0.02808,    //  5Y
			0.02983,    //  6Y
			0.03136,    //  7Y
			0.03268,    //  8Y
			0.03383,    //  9Y
			0.03488,    // 10Y
			0.03583,    // 11Y
			0.03668,    // 12Y
			0.03833,    // 15Y
			0.03854,    // 20Y
			0.03672,    // 25Y
			0.03510,    // 30Y
			0.03266,    // 40Y
			0.03145     // 50Y
		};

		return ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
			"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
			new ValuationParams (spotDate, spotDate, currency),
			DepositInstrumentsFromMaturityDays (
				spotDate,
				new int[]
				{
					1,
					2,
					3,
					7,
					14,
					21,
					30,
					60
				},
				0,
				currency
			),
			new double[]
			{
				0.0120,
				0.0120,
				0.0120,
				0.0145,
				0.0155,
				0.0160,
				0.0166,
				0.0185
			},
			new String[]
			{
				"ForwardRate",
				"ForwardRate",
				"ForwardRate",
				"ForwardRate",
				"ForwardRate",
				"ForwardRate",
				"ForwardRate",
				"ForwardRate"
			},
			SwapInstrumentsFromMaturityTenor (
				spotDate,
				currency,
				new String[]
				{
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
					"30Y",
					"40Y",
					"50Y"
				},
				swapQuoteArray
			),
			swapQuoteArray,
			new String[]
			{
				"SwapRate",    //  4Y
				"SwapRate",    //  5Y
				"SwapRate",    //  6Y
				"SwapRate",    //  7Y
				"SwapRate",    //  8Y
				"SwapRate",    //  9Y
				"SwapRate",    // 10Y
				"SwapRate",    // 11Y
				"SwapRate",    // 12Y
				"SwapRate",    // 15Y
				"SwapRate",    // 20Y
				"SwapRate",    // 25Y
				"SwapRate",    // 30Y
				"SwapRate",    // 40Y
				"SwapRate"     // 50Y
			},
			true
		);
	}

	private static final double ATMCall (
		final JulianDate maturityDate,
		final ValuationParams valuationParams,
		final MergedDiscountForwardCurve discountCurve,
		final double volatility,
		final String measure)
		throws Exception
	{
		return new EuropeanCallPut (
			maturityDate,
			1.
		).value (
			valuationParams,
			1.,
			false,
			discountCurve,
			new Flat (volatility),
			new BlackScholesAlgorithm()
		).get (
			measure
		);
	}

	private static final void InputNodeReplicator (
		final NodeStructure nodeStructure,
		final String[] maturityTenorArray,
		final double[] nodeInputArray)
		throws Exception
	{
		System.out.println ("\n\t" + nodeStructure.label());

		System.out.println ("\n\t|--------------------------|");

		System.out.println ("\t| TNR =>   CALC  |  INPUT  |");

		System.out.println ("\t|--------------------------|");

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			System.out.println (
				"\t| " + maturityTenorArray[maturityTenorIndex] + " => " + FormatUtil.FormatDouble (
					nodeStructure.node (maturityTenorArray[maturityTenorIndex]),
					2,
					2,
					100.
				) + "% | " + FormatUtil.FormatDouble (
					nodeInputArray[maturityTenorIndex],
					2,
					2,
					100.
				) + "% |");
		}

		System.out.println ("\t|--------------------------|");
	}

	private static final void OffGrid (
		final String header,
		final String[] labelArray,
		final NodeStructure[] nodeStructureArray,
		final String[] maturityTenorArray)
		throws Exception
	{
		System.out.println ("\n\n\t\t" + header + "\n");

		System.out.print ("\t| TNR =>");

		for (int nodeStructureIndex = 0;
			nodeStructureIndex < nodeStructureArray.length;
			++nodeStructureIndex)
		{
			System.out.print (" " + labelArray[nodeStructureIndex] + " | ");
		}

		System.out.println ("\n");

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			System.out.print ("\t| " + maturityTenorArray[maturityTenorIndex] + " =>");

			for (int nodeStructureIndex = 0;
				nodeStructureIndex < nodeStructureArray.length;
				++nodeStructureIndex)
			{
				System.out.print (
					"  " + FormatUtil.FormatDouble (
						nodeStructureArray[nodeStructureIndex].node (maturityTenorArray[maturityTenorIndex]),
						2,
						2,
						100.
					) + "%   | "
				);
			}

			System.out.print ("\n");
		}

		System.out.println ("\n");
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

		String[] maturityTenorArray =
		{
			"06M",
			"01Y",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"07Y",
			"10Y",
			"15Y",
			"20Y"
		};
		double[] volatilityArray =
		{
			0.20,
			0.23,
			0.27,
			0.30,
			0.33,
			0.35,
			0.34,
			0.29,
			0.26,
			0.19
		};
		String[] offGridTenorArray =
		{
			"03M",
			"09M",
			"18M",
			"30Y",
			"42M",
			"54M",
			"06Y",
			"09Y",
			"12Y",
			"18Y",
			"25Y"
		};

		JulianDate today = DateUtil.Today();

		MergedDiscountForwardCurve discountCurve = MakeDC (today, "USD");

		double[] callPriceArray = new double[volatilityArray.length];
		double[] impliedCallVolatilityArray = new double[volatilityArray.length];

		ValuationParams valuationParams = new ValuationParams (today, today, "USD");

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			JulianDate maturityDate = today.addTenor (maturityTenorArray[maturityTenorIndex]);

			callPriceArray[maturityTenorIndex] = ATMCall (
				maturityDate,
				valuationParams,
				discountCurve,
				volatilityArray[maturityTenorIndex],
				"CallPrice"
			);

			impliedCallVolatilityArray[maturityTenorIndex] = ATMCall (
				maturityDate,
				valuationParams,
				discountCurve,
				volatilityArray[maturityTenorIndex],
				"ImpliedCallVolatility"
			);
		}

		NodeStructure callPriceCubicPolynomialNodeStructure =
			ScenarioTermStructureBuilder.CubicPolynomialTermStructure (
				"CUBIC_POLY_CALLPRICE_TERMSTRUCTURE",
				today,
				"USD",
				maturityTenorArray,
				callPriceArray
			);

		InputNodeReplicator (callPriceCubicPolynomialNodeStructure, maturityTenorArray, callPriceArray);

		NodeStructure callVolatilityCubicPolynomialNodeStructure =
			ScenarioTermStructureBuilder.CubicPolynomialTermStructure (
				"CUBIC_POLY_CALLVOL_TERMSTRUCTURE",
				today,
				"USD",
				maturityTenorArray,
				impliedCallVolatilityArray
			);

		InputNodeReplicator (
			callVolatilityCubicPolynomialNodeStructure,
			maturityTenorArray,
			impliedCallVolatilityArray
		);

		OffGrid (
			"ATM_CALLPRICE_TERM_STRUCTURE",
			new String[]
			{
				"Cubic Poly",
				"Quart Poly",
				"KaklisPand",
				"KLKHyperbl",
				"KLKRatlLin",
				"KLKRatlQua"
			},
			new NodeStructure[]
			{
				callPriceCubicPolynomialNodeStructure,
				ScenarioTermStructureBuilder.QuarticPolynomialTermStructure (
					"QUARTIC_POLY_CALLPRICE_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					callPriceArray
				),
				ScenarioTermStructureBuilder.KaklisPandelisTermStructure (
					"KAKLIS_PANDELIS_CALLPRICE_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					callPriceArray
				),
				ScenarioTermStructureBuilder.KLKHyperbolicTermStructure (
					"KLK_HYPERBOLIC_CALLPRICE_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					callPriceArray,
					1.
				),
				ScenarioTermStructureBuilder.KLKRationalLinearTermStructure (
					"KLK_RATIONAL_LINEAR_CALLPRICE_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					callPriceArray,
					1.
				),
				ScenarioTermStructureBuilder.KLKRationalQuadraticTermStructure (
					"KLK_RATIONAL_QUADRATIC_CALLPRICE_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					callPriceArray,
					0.0001
				)
			},
			offGridTenorArray
		);

		OffGrid (
			"ATM_CALLVOL_TERM_STRUCTURE",
			new String[]
			{
				"Cubic Poly",
				"Quart Poly",
				"KaklisPand",
				"KLKHyperbl",
				"KLKRatlLin",
				"KLKRatlQua"
			},
			new NodeStructure[]
			{
				callVolatilityCubicPolynomialNodeStructure,
				ScenarioTermStructureBuilder.QuarticPolynomialTermStructure (
					"QUARTIC_POLY_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray
				),
				ScenarioTermStructureBuilder.KaklisPandelisTermStructure (
					"KAKLIS_PANDELIS_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray
				),
				ScenarioTermStructureBuilder.KLKHyperbolicTermStructure (
					"KLK_HYPERBOLIC_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray,
					1.
				),
				ScenarioTermStructureBuilder.KLKRationalLinearTermStructure (
					"KLK_RATIONAL_LINEAR_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray,
					1.
				),
				ScenarioTermStructureBuilder.KLKRationalQuadraticTermStructure (
					"KLK_RATIONAL_QUADRATIC_CALLVOL_TERMSTRUCTURE",
					today,
					"USD",
					maturityTenorArray,
					impliedCallVolatilityArray,
					0.0001
				)
			},
			offGridTenorArray
		);

		EnvManager.TerminateEnv();
	}
}
