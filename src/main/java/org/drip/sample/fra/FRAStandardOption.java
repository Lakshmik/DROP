
package org.drip.sample.fra;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1.*;
import org.drip.function.r1tor1custom.AndersenPiterbargMeanReverter;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.fra.*;
import org.drip.product.params.LastTradingDateSetting;
import org.drip.product.rates.*;
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
 * <i>FRAStandardOption</i> contains the demonstration of the Valuation of an Option on a Multi-Curve FRA
 * 	Standard.
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/fra/README.md">Multi-Curve FRA Market/Standard</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FRAStandardOption
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

	private static final FloatFloatComponent[] MakexM6MBasisSwap (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final int tenorInMonths)
		throws Exception
	{
		String tenor = tenorInMonths + "M";
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			floatFloatComponentArray[tenorIndex] =
				OTCFloatFloat (spotDate, currency, tenor, maturityTenorArray[tenorIndex], 0.);
		}

		return floatFloatComponentArray;
	}

	private static final ForwardCurve MakeFC (
		final JulianDate spotDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final int tenorInMonths,
		final String[] xM6MFwdTenorArray,
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
			MakexM6MBasisSwap (spotDate, currency, xM6MFwdTenorArray, tenorInMonths),
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

		String tenor = "3M";
		String currency = "USD";
		double forwardVolatility = 0.3;
		double fundingVolatility = 0.1;
		double forwardFundingCorrelation = 0.2;
		String manifestMeasure = "QuantoAdjustedParForward";
		double meanReverterHazardCorrelation = 0.4 / 365.25;

		JulianDate today = DateUtil.Today().addTenor ("0D");

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		MergedDiscountForwardCurve discountCurve = MakeDC (today, currency);

		FRAStandardComponent fra = SingleStreamComponentBuilder.FRAStandard (
			today.addTenor (tenor),
			forwardLabel,
			0.006
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			MakeFC (today, currency,discountCurve).get (tenor),
			null,
			null,
			null,
			null,
			null,
			null
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		int valuationDateJulian = today.julian();

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDateJulian,
				VolatilityLabel.Standard (forwardLabel),
				forwardLabel.currency(),
				forwardVolatility
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
			new AndersenPiterbargMeanReverter (
				new ExponentialDecay (valuationDateJulian, meanReverterHazardCorrelation),
				new Flat (forwardFundingCorrelation)
			)
		);

		double strike = 1.01 * fra.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		).get (
			manifestMeasure
		);

		FRAStandardCapFloorlet fraCaplet = new FRAStandardCapFloorlet (
			fra.name() + "::CAPLET",
			fra,
			manifestMeasure,
			true,
			strike,
			1.,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			new BlackScholesAlgorithm(),
			null
		);

		Map<String, Double> fraCapletMeasureMap = fraCaplet.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		System.out.println ("\t||-----------------------------------------------------------------|\\n");

		System.out.println ("\t||-----------------------------------------------------------------|");

		for (Map.Entry<String, Double> fraCapletMeasureMapEntry : fraCapletMeasureMap.entrySet()) {
			System.out.println (
				"\t||" + fraCapletMeasureMapEntry.getKey() + " => " + fraCapletMeasureMapEntry.getValue()
			);
		}

		System.out.println ("\t||-----------------------------------------------------------------|\\n");

		System.out.println ("\t||-----------------------------------------------------------------|");

		FRAStandardCapFloorlet fraFloorlet = new FRAStandardCapFloorlet (
			fra.name() + "::FLOORLET",
			fra,
			manifestMeasure,
			false,
			strike,
			1.,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			new BlackScholesAlgorithm(),
			null
		);

		Map<String, Double> fraFloorletMeasureMap = fraFloorlet.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		for (Map.Entry<String, Double> fraFloorletMeasureMapEntry : fraFloorletMeasureMap.entrySet()) {
			System.out.println (
				"\t||" + fraFloorletMeasureMapEntry.getKey() + " => " + fraFloorletMeasureMapEntry.getValue()
			);
		}

		System.out.println ("\n\t||------------------------------------------------------------------|");

		System.out.println ("\t||------------------------------------------------------------------|\n");

		System.out.println (
			"\t|| Price Implied FRA Caplet Vol       : " + FormatUtil.FormatDouble (
				fraCaplet.implyVolatility (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"Price",
					fraCapletMeasureMap.get ("Price")
				),
				1,
				2,
				100.
			) + "%"
		);

		System.out.println (
			"\t|| ATM Price Implied FRA Caplet Vol   : " + FormatUtil.FormatDouble (
				fraCaplet.implyVolatility (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"ATMPrice",
					fraCapletMeasureMap.get ("ATMPrice")
				),
				1,
				2,
				100.
			) + "%"
		);

		System.out.println (
			"\t|| Price Implied FRA Floorlet Vol     : " + FormatUtil.FormatDouble (
				fraFloorlet.implyVolatility (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"Price",
					fraFloorletMeasureMap.get ("Price")
				),
				1,
				2,
				100.
			) + "%"
		);

		System.out.println (
			"\t|| ATM Price Implied FRA Floorlet Vol : " + FormatUtil.FormatDouble (
				fraFloorlet.implyVolatility (
					valuationParams,
					null,
					curveSurfaceQuoteContainer,
					null,
					"ATMPrice",
					fraFloorletMeasureMap.get ("ATMPrice")
				),
				1,
				2,
				100.
			) + "%"
		);

		EnvManager.TerminateEnv();
	}
}
