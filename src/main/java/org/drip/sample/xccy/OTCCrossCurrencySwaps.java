
package org.drip.sample.xccy;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.product.params.CurrencyPair;
import org.drip.product.rates.FloatFloatComponent;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.FXLabel;
import org.drip.state.identifier.ForwardLabel;
import org.drip.state.identifier.FundingLabel;
import org.drip.state.identifier.VolatilityLabel;

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
 * <i>OTCCrossCurrencySwaps</i> demonstrates the Construction and Valuation of the Cross-Currency Floating
 * 	Swap of OTC contracts.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/xccy/README.md">OTC Cross Currency Swaps Definition</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class OTCCrossCurrencySwaps
{

	private static final FloatFloatComponent OTCCrossCurrencyFloatFloat (
		final String referenceCurrency,
		final String derivedCurrency,
		final JulianDate spotDate,
		final String maturityTenor,
		final double basis,
		final double derivedNotionalScaler)
	{
		return CrossFloatConventionContainer.ConventionFromJurisdiction (
			referenceCurrency,
			derivedCurrency
		).createFloatFloatComponent (
			spotDate,
			maturityTenor,
			basis,
			1.,
			-1. * derivedNotionalScaler
		);
	}

	private static final void OTCCrossCurrencyRun (
		final JulianDate spotDate,
		final String referenceCurrency,
		final String derivedCurrency,
		final String maturityTenor,
		final double basis,
		final double referenceDerivedFXRate)
		throws Exception
	{
		double derived3MForwardRate = 0.02;
		double referenceFundingRate = 0.02;

		double derivedForward3MVolatility = 0.3;
		double referenceFundingVolatility = 0.3;
		double referenceDerivedFXVolatility = 0.3;

		double referenceFundingDerived3MCorrelation = 0.1;
		double derived3MReferenceDerivedFXCorrelation = 0.1;
		double referenceFundingReferenceDerivedFXCorrelation = 0.1;

		MergedDiscountForwardCurve referenceFundingCurve =
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (
				spotDate,
				referenceCurrency,
				referenceFundingRate
			);

		ForwardLabel derived3MForwardLabel = ForwardLabel.Create (derivedCurrency, "3M");

		ForwardCurve derived3MForwardCurve = ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
			spotDate,
			derived3MForwardLabel,
			derived3MForwardRate
		);

		CurrencyPair currencyPair = CurrencyPair.FromCode (referenceCurrency + "/" + derivedCurrency);

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		FundingLabel referenceFundingLabel = FundingLabel.Standard (referenceCurrency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setForwardState (derived3MForwardCurve);

		curveSurfaceQuoteContainer.setFundingState (referenceFundingCurve);

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				"FX::" + currencyPair.code(),
				spotDate,
				currencyPair,
				new String[] {
					"10Y"
				},
				new double[] {
					referenceDerivedFXRate
				},
				referenceDerivedFXRate
			)
		);

		int spotDateJulian = spotDate.julian();

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				spotDateJulian,
				VolatilityLabel.Standard (derived3MForwardLabel),
				derivedCurrency,
				derivedForward3MVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				spotDateJulian,
				VolatilityLabel.Standard (referenceFundingLabel),
				referenceCurrency,
				referenceFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setFXVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				spotDateJulian,
				VolatilityLabel.Standard (fxLabel),
				derivedCurrency,
				referenceDerivedFXVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			derived3MForwardLabel,
			referenceFundingLabel,
			new Flat (referenceFundingDerived3MCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			derived3MForwardLabel,
			fxLabel,
			new Flat (derived3MReferenceDerivedFXCorrelation)
		);

		curveSurfaceQuoteContainer.setFundingFXCorrelation (
			referenceFundingLabel,
			fxLabel,
			new Flat (referenceFundingReferenceDerivedFXCorrelation)
		);

		FloatFloatComponent xccySwap = OTCCrossCurrencyFloatFloat (
			referenceCurrency,
			derivedCurrency,
			spotDate,
			maturityTenor,
			basis,
			1. / referenceDerivedFXRate
		);

		xccySwap.setPrimaryCode (
			derivedCurrency + "_" + referenceCurrency + "_OTC::FLOATFLOAT::" + maturityTenor
		);

		curveSurfaceQuoteContainer.setFixing (
			xccySwap.effectiveDate(),
			fxLabel,
			referenceDerivedFXRate
		);

		ValuationParams valParams = new ValuationParams (
			spotDate,
			spotDate,
			referenceCurrency + "," + derivedCurrency
		);

		CaseInsensitiveTreeMap<Double> mapXCcyOutput = xccySwap.value (
			valParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		System.out.println (
			"\t| " + xccySwap.name() +
			"  [" + xccySwap.effectiveDate() + " -> " + xccySwap.maturityDate() + "]  =>  " +
			FormatUtil.FormatDouble (mapXCcyOutput.get ("Price"), 1, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (mapXCcyOutput.get ("DerivedParBasisSpread"), 1, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (mapXCcyOutput.get ("ReferenceParBasisSpread"), 1, 2, 1.) + "  |  " +
			FormatUtil.FormatDouble (mapXCcyOutput.get ("DerivedCleanDV01"), 1, 2, 10000.) + "  |  " +
			FormatUtil.FormatDouble (mapXCcyOutput.get ("ReferenceCleanDV01"), 1, 2, 10000.) + "  |"
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

		JulianDate spotDate = DateUtil.Today();

		System.out.println ("\t---------------------------------------------------------");

		System.out.println ("\t\tCROSS-CURRENCY FLOAT-FLOAT COMPONENT RUNS");

		System.out.println ("\t---------------------------------------------------------");

		System.out.println ("\tL -> R:");

		System.out.println ("\t\tCross Currency Swap Name");

		System.out.println ("\t\tFloat-Float Effective");

		System.out.println ("\t\tFloat-Float Maturity");

		System.out.println ("\t\tPrice");

		System.out.println ("\t\tDerived Stream Par Basis Spread");

		System.out.println ("\t\tReference Stream Par Basis Spread");

		System.out.println ("\t\tAnnualized Derived Stream Duration");

		System.out.println ("\t\tAnnualized Reference Stream Duration");

		System.out.println (
			"\t------------------------------------------------------------------------------------------------------------------"
		);

		OTCCrossCurrencyRun (spotDate, "USD", "AUD", "2Y", 0.0003, 0.7769);

		OTCCrossCurrencyRun (spotDate, "USD", "CAD", "2Y", 0.0003, 0.7861);

		OTCCrossCurrencyRun (spotDate, "USD", "CHF", "2Y", 0.0003, 1.0811);

		OTCCrossCurrencyRun (spotDate, "USD", "CLP", "2Y", 0.0003, 0.0016);

		OTCCrossCurrencyRun (spotDate, "USD", "DKK", "2Y", 0.0003, 0.1517);

		OTCCrossCurrencyRun (spotDate, "USD", "EUR", "2Y", 0.0003, 1.1294);

		OTCCrossCurrencyRun (spotDate, "USD", "GBP", "2Y", 0.0003, 1.5004);

		OTCCrossCurrencyRun (spotDate, "USD", "JPY", "2Y", 0.0003, 0.0085);

		OTCCrossCurrencyRun (spotDate, "USD", "MXN", "2Y", 0.0003, 0.0666);

		OTCCrossCurrencyRun (spotDate, "USD", "NOK", "2Y", 0.0003, 0.1288);

		OTCCrossCurrencyRun (spotDate, "USD", "PLN", "2Y", 0.0003, 0.2701);

		OTCCrossCurrencyRun (spotDate, "USD", "SEK", "2Y", 0.0003, 0.1211);

		System.out.println (
			"\t------------------------------------------------------------------------------------------------------------------"
		);

		EnvManager.TerminateEnv();
	}
}
