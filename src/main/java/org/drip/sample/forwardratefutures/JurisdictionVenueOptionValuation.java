
package org.drip.sample.forwardratefutures;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamOptionBuilder;
import org.drip.product.rates.*;
import org.drip.sample.forward.OvernightIndexCurve;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.stretch.MultiSegmentSequenceBuilder;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.*;

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
 * <i>JurisdictionVenueOptionValuation</i> contains the Demonstration of the Construction and the Valuation
 * 	of the Options on Standardized LIBOR Futures Contract across Jurisdictions and Venues.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/forwardratefutures/README.md">Jurisdiction IRS Futures Options Definition</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class JurisdictionVenueOptionValuation
{

	private static final FloatFloatComponent OTCFloatFloat (
		final JulianDate spotDate,
		final String currency,
		final String derivedTenor,
		final String maturityTenor,
		final double basis)
	{
		return IBORFloatFloatContainer.ConventionFromJurisdiction (
			currency
		).createFloatFloatComponent (
			spotDate,
			derivedTenor,
			maturityTenor,
			basis,
			1.
		);
	}

	private static final FloatFloatComponent[] MakexM6MBasisSwap (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final int tenorInMonths)
		throws Exception
	{
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		for (int floatFloatComponent = 0;
			floatFloatComponent < maturityTenorArray.length;
			++floatFloatComponent)
		{
			floatFloatComponentArray[floatFloatComponent] = OTCFloatFloat (
				spotDate,
				currency,
				tenorInMonths + "M",
				maturityTenorArray[floatFloatComponent],
				0.
			);
		}

		return floatFloatComponentArray;
	}

	private static final ForwardCurve MakeFC (
		final JulianDate spotDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final int tenorInMonths,
		final String[] xM6MForwardTenorArray,
		final double[] xM6MBasisSwapQuoteArray)
		throws Exception
	{
		String basisTenor = tenorInMonths + "M";

		return ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			"QUARTIC_FWD" + basisTenor,
			ForwardLabel.Create (currency, basisTenor),
			new ValuationParams (spotDate, spotDate, currency),
			null,
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
			null,
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (5),
			MakexM6MBasisSwap (spotDate, currency, xM6MForwardTenorArray, tenorInMonths),
			"DerivedParBasisSpread",
			xM6MBasisSwapQuoteArray,
			discountCurve.forward (spotDate.julian(), spotDate.addTenor (basisTenor).julian())
		);
	}

	private static final Map<String, ForwardCurve> MakeFC (
		final JulianDate date,
		final String currency,
		final MergedDiscountForwardCurve discountCurve)
		throws Exception
	{
		Map<String, ForwardCurve> forwardCurveMap = new HashMap<String, ForwardCurve>();

		forwardCurveMap.put (
			"1M",
			MakeFC (
				date,
				currency,
				discountCurve,
				1,
				new String[]
				{
					"1Y",
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
				new double[]
				{
					0.00551,    //  1Y
					0.00387,    //  2Y
					0.00298,    //  3Y
					0.00247,    //  4Y
					0.00211,    //  5Y
					0.00185,    //  6Y
					0.00165,    //  7Y
					0.00150,    //  8Y
					0.00137,    //  9Y
					0.00127,    // 10Y
					0.00119,    // 11Y
					0.00112,    // 12Y
					0.00096,    // 15Y
					0.00079,    // 20Y
					0.00069,    // 25Y
					0.00062     // 30Y
				}
			)
		);

		forwardCurveMap.put (
			"3M",
			MakeFC (
				date,
				currency,
				discountCurve,
				3,
				new String[]
				{
					"1Y",
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
				new double[]
				{
					0.00186,    //  1Y
					0.00127,    //  2Y
					0.00097,    //  3Y
					0.00080,    //  4Y
					0.00067,    //  5Y
					0.00058,    //  6Y
					0.00051,    //  7Y
					0.00046,    //  8Y
					0.00042,    //  9Y
					0.00038,    // 10Y
					0.00035,    // 11Y
					0.00033,    // 12Y
					0.00028,    // 15Y
					0.00022,    // 20Y
					0.00020,    // 25Y
					0.00018     // 30Y
				}
			)
		);

		forwardCurveMap.put (
			"12M",
			MakeFC (
				date,
				currency,
				discountCurve,
				12,
				new String[]
				{
					"1Y",
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
					"30Y",
					"35Y",
					"40Y" // Extrapolated
				},
				new double[]
				{
					-0.00212,    //  1Y
					-0.00152,    //  2Y
					-0.00117,    //  3Y
					-0.00097,    //  4Y
					-0.00082,    //  5Y
					-0.00072,    //  6Y
					-0.00063,    //  7Y
					-0.00057,    //  8Y
					-0.00051,    //  9Y
					-0.00047,    // 10Y
					-0.00044,    // 11Y
					-0.00041,    // 12Y
					-0.00035,    // 15Y
					-0.00028,    // 20Y
					-0.00025,    // 25Y
					-0.00022,    // 30Y
					-0.00022,    // 35Y Extrapolated
					-0.00022,    // 40Y Extrapolated
				}
			)
		);

		return forwardCurveMap;
	}

	private static final void SetVolCorrelation (
		final int valueDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel,
		final double forwardVolatility,
		final double fundingVolatility,
		final double forwardFundingCorrelation)
		throws Exception
	{
		String currency = forwardLabel.currency();

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardLabel),
				currency,
				forwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (fundingLabel),
				forwardLabel.currency(),
				fundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel,
			fundingLabel,
			new Flat (forwardFundingCorrelation)
		);
	}

	private static final void FuturesOptionMetrics (
		final String currency,
		final String tenor,
		final JulianDate spotDate,
		final String optionType,
		final String exchange)
		throws Exception
	{
		MergedDiscountForwardCurve oisDiscountCurve = OvernightIndexCurve.MakeDC (spotDate, currency);

		ForwardCurve forwardCurve = MakeFC (spotDate, currency, oisDiscountCurve).get (tenor);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		JulianDate dtEffective = spotDate.addTenor ("3M");

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			oisDiscountCurve,
			forwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		double forwardVolatility = 0.5;
		double fundingVolatility = 0.5;
		double forwardFundingCorrelation = 0.5;

		SetVolCorrelation (
			spotDate.julian(),
			curveSurfaceQuoteContainer,
			forwardLabel,
			forwardVolatility,
			fundingVolatility,
			forwardFundingCorrelation
		);

		Map<String, Double> futuresOptionMeasureMap = SingleStreamOptionBuilder.ExchangeTradedFuturesOption (
			dtEffective,
			forwardLabel,
			forwardCurve.forward (dtEffective.addTenor (forwardCurve.tenor())),
			"ParForward",
			false,
			optionType,
			exchange
		).value (
			new ValuationParams (spotDate, spotDate, currency),
			null,
			curveSurfaceQuoteContainer,
			null
		);

		System.out.println (
			"\t\t" + exchange + " | " + FormatUtil.FormatDouble (
				futuresOptionMeasureMap.get ("ATMFRA"),
				1,
				4,
				100.
			) + " % | " + FormatUtil.FormatDouble (
				futuresOptionMeasureMap.get ("Upfront"),
				1,
				1,
				10000.
			) + " bp | " + forwardLabel.fullyQualifiedName()
		);
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

		JulianDate today = DateUtil.Today();

		System.out.println ("\t||-----------------------------------------------------");

		System.out.println ("\t||Output Order - L -> R:");

		System.out.println ("\t||-----------------------------------------------------");

		System.out.println (
			"\t\t|| Exchange\n\t\t|| ATM Par FRA Level (%)\n\t\t|| Option Upfront (bp)\n\t\t|| FRA Label"
		);

		System.out.println ("\n\t||-----------------------------------------------------");

		System.out.println ("\t||--------------- MARGIN TYPE OPTION ------------------");

		System.out.println ("\t||-----------------------------------------------------");

		FuturesOptionMetrics ("CHF", "3M", today, "MARGIN", "LIFFE");

		FuturesOptionMetrics ("GBP", "3M", today, "MARGIN", "LIFFE");

		/* FuturesOptionMetrics ("EUR", "3M", today, "MARGIN", "LIFFE"); */

		FuturesOptionMetrics ("USD", "3M", today, "MARGIN", "LIFFE");

		System.out.println ("\t||-----------------------------------------------------");

		System.out.println ("\t||-------------- PREMIUM TYPE OPTION ------------------");

		System.out.println ("\t||-----------------------------------------------------");

		FuturesOptionMetrics ("JPY", "3M", today, "PREMIUM", "SGX");

		FuturesOptionMetrics ("USD", "1M", today, "PREMIUM", "CME");

		FuturesOptionMetrics ("USD", "3M", today, "PREMIUM", "CME");

		FuturesOptionMetrics ("USD", "3M", today, "PREMIUM", "SGX");

		System.out.println ("\t||-----------------------------------------------------");

		System.out.println ("\t||-----------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
