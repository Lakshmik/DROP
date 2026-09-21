
package org.drip.sample.lmm;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.MarketSurface;
import org.drip.dynamics.lmm.LognormalLIBORVolatility;
import org.drip.market.otc.*;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.FixFloatComponent;
import org.drip.sequence.random.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
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
 * <i>TwoFactorLIBORVolatility</i> demonstrates the Construction and Usage of the 2 Factor LIBOR Forward Rate
 * 	Volatility. The References are:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): Log-normality of Rates and Term Structure
 *  			Models, The University of New South Wales.
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): Nominal Annual Rates and Log-normal Volatility Structure, The University of
 *  			New South Wales.
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics,
 * 				Mathematical Finance 7 (2), 127-155.
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/lmm/README.md">LMM Multi-Factor Monte Carlo</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class TwoFactorLIBORVolatility
{

	private static final FixFloatComponent OTCFixFloat (
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
		final int[] maturityDaysArray,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibratableComponentArray =
			new CalibratableComponent[maturityDaysArray.length + futuresCount];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			calibratableComponentArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				ForwardLabel.Create (currency, "3M")
			);
		}

		CalibratableComponent[] futuresArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int componentIndex = maturityDaysArray.length;
			componentIndex < maturityDaysArray.length + futuresCount;
			++componentIndex)
		{
			calibratableComponentArray[componentIndex] =
				futuresArray[componentIndex - maturityDaysArray.length];
		}

		return calibratableComponentArray;
	}

	private static final CalibratableComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int irsIndex = 0; irsIndex < maturityTenorArray.length; ++irsIndex) {
			irsArray[irsIndex] = OTCFixFloat (
				spotDate,
				currency,
				maturityTenorArray[irsIndex],
				couponArray[irsIndex]
			);
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
			false
		);
	}

	private static final MarketSurface FactorFlatVolatilitySurface (
		final JulianDate startDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] factorFlatVolatilityArray,
		final double[] termStructureLoadingArray)
		throws Exception
	{
		double[] maturityDateArray = new double[maturityTenorArray.length + 1];
		double[][] volatilityGrid = new double[maturityTenorArray.length + 1][maturityTenorArray.length + 1];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length + 1; ++tenorIndex) {
			maturityDateArray[tenorIndex] = 0 == tenorIndex ?
				maturityDateArray[tenorIndex] = startDate.julian() :
				startDate.addTenor (maturityTenorArray[tenorIndex - 1]).julian();
		}

		for (int tenorIndexI = 0; tenorIndexI < maturityTenorArray.length + 1; ++tenorIndexI) {
			for (int tenorIndexJ = 0; tenorIndexJ < maturityTenorArray.length + 1; ++tenorIndexJ) {
				volatilityGrid[tenorIndexI][tenorIndexJ] = 0 == tenorIndexI || 0 == tenorIndexJ ?
					factorFlatVolatilityArray[0] :
					termStructureLoadingArray[tenorIndexI - 1] * factorFlatVolatilityArray[tenorIndexJ - 1];
			}
		}

		return ScenarioMarketSurfaceBuilder.CustomSplineWireSurface (
			"VIEW_TARGET_VOLATILITY_SURFACE",
			startDate,
			currency,
			maturityDateArray,
			maturityDateArray,
			volatilityGrid,
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (2),
				SegmentInelasticDesignControl.Create (0, 2),
				null,
				null
			),
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
				new PolynomialFunctionSetParams (4),
				SegmentInelasticDesignControl.Create (2, 2),
				null,
				null
			)
		);
	}

	private static final void DisplayVolArray (
		final String tenor,
		final double[] volatilityArray)
	{
		String dump = "\t | " + tenor + " =>  ";

		for (int volatilityIndex = 0; volatilityIndex < volatilityArray.length; ++volatilityIndex) {
			dump += FormatUtil.FormatDouble (volatilityArray[volatilityIndex], 1, 2, 100.) + "% | ";
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

		JulianDate spotDate = DateUtil.CreateFromYMD (1995, DateUtil.FEBRUARY, 3);

		String fraTenor = "3M";
		String currency = "GBP";
		String[] maturityTenorArray =
		{
			 "3M",
			 "6M",
			"12M",
			"18M",
			"24M",
			"30M",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			 "9Y",
			"11Y"
		};
		double[] flatTermStructureArray =
		{
			1.00000000, //  "3M",
			1.00000000, //  "6M",
			0.99168448, // "12M",
			1.00388389, // "18M",
			1.00388389, // "24M",
			1.07602593, // "30M",
			1.07602593, //  "3Y",
			1.04727642, //  "4Y",
			1.02727799, //  "5Y",
			0.96660430, //  "7Y",
			0.93012459, //  "9Y",
			0.81425256  // "11Y"
		};
		double[] flatVolatilityFactorArray1 =
		{
			0.09481393, //  "3M",
			0.08498925, //  "6M",
			0.22939966, // "12M",
			0.19166872, // "18M",
			0.08232925, // "24M",
			0.18548202, // "30M",
			0.13817885, //  "3Y",
			0.08562258, //  "4Y",
			0.14547123, //  "5Y",
			0.08869328, //  "7Y",
			0.04121240, //  "9Y",
			0.15206796  // "11Y"
		};
		double[] flatVolatilityFactorArray2 =
		{
			 0.12146092, //  "3M",
			 0.05117321, //  "6M",
			 0.09100802, // "12M",
			 0.02876211, // "18M",
			 0.01172983, // "24M",
			 0.00047705, // "30M",
			-0.01160086, //  "3Y",
			-0.04673283, //  "4Y",
			-0.04181446, //  "5Y",
			-0.05459175, //  "7Y",
			-0.03631021, //  "9Y",
			-0.16626765  // "11Y"
		};
		double[][] correlationMatrix =
		{
			{
				1.0,
				0.1
			},
			{
				0.1,
				1.0
			},
		};
		String[] forwardTenorArray =
		{
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y"
		};

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, fraTenor);

		ForwardCurve nativeForwardCurve = MakeDC (spotDate, currency).nativeForwardCurve (fraTenor);

		UnivariateSequenceGenerator univariateSequenceGenerator = new BoxMullerGaussian (0., 1.);

		LognormalLIBORVolatility lognormalLIBORVolatility = new LognormalLIBORVolatility (
			spotDate.julian(),
			forwardLabel,
			new MarketSurface[]
			{
				FactorFlatVolatilitySurface (
					spotDate,
					currency,
					maturityTenorArray,
					flatVolatilityFactorArray1,
					flatTermStructureArray
				),
				FactorFlatVolatilitySurface (
					spotDate,
					currency,
					maturityTenorArray,
					flatVolatilityFactorArray2,
					flatTermStructureArray
				)
			},
			new PrincipalFactorSequenceGenerator (
				new UnivariateSequenceGenerator[]
				{
					univariateSequenceGenerator,
					univariateSequenceGenerator
				},
				correlationMatrix,
				2
			)
		);

		System.out.println ("\n\t |------------------------------|");

		System.out.println ("\t |  CONTINUOUS FORWARD RATE VOL |");

		System.out.println ("\t |------------------------------|");

		for (String forwardTenor : forwardTenorArray) {
			DisplayVolArray (
				forwardTenor,
				lognormalLIBORVolatility.continuousForwardVolatility (
					spotDate.addTenor (forwardTenor).julian(),
					nativeForwardCurve
				)
			);
		}

		System.out.println ("\t |------------------------------|");

		EnvManager.TerminateEnv();
	}
}
