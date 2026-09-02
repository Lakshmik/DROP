
package org.drip.sample.cross;

import java.util.*;

import org.drip.analytics.date.DateUtil;
import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.numerical.common.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.fx.ComponentPair;
import org.drip.product.params.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
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
 * <i>FloatFloatFloatFloat</i> demonstrates the construction, the usage, and the eventual valuation of the
 * 	Cross Currency Basis Swap built out of a pair of float-float swaps.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/cross/README.md">Single/Dual Stream XCCY Component</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FloatFloatFloatFloat
{

	private static final FloatFloatComponent MakeFloatFloatSwap (
		final JulianDate effectiveDate,
		final boolean fxMTM,
		final String payCurrency,
		final String couponCurrency,
		final String maturityTenor,
		final int tenorInMonthsReference,
		final int tenorInMonthsDerived)
		throws Exception
	{
		String derivedTenor = tenorInMonthsDerived + "M";
		String referenceTenor = tenorInMonthsReference + "M";

		FixingSetting fixingSetting = fxMTM ? null : new FixingSetting (
			FixingSetting.FIXING_PRESET_STATIC,
			null,
			effectiveDate.julian()
		);

		return new FloatFloatComponent (
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						referenceTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonthsReference,
						referenceTenor,
						payCurrency,
						null,
						-1.,
						null,
						null,
						fixingSetting,
						null
					),
					new ComposableFloatingUnitSetting (
						referenceTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (couponCurrency, tenorInMonthsReference + "M"),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						derivedTenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonthsDerived,
						derivedTenor,
						payCurrency,
						null,
						1.,
						null,
						null,
						fixingSetting,
						null
					),
					new ComposableFloatingUnitSetting (
						derivedTenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (couponCurrency, derivedTenor),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new CashSettleParams (0, payCurrency, 0)
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

		String derivedCurrency = "EUR";
		String referenceCurrency = "USD";

		double referenceFundingRate = 0.02;
		double derived3MForwardRate = 0.00375;
		double derived6MForwardRate = 0.00625;
		double reference3MForwardRate = 0.00750;
		double reference6MForwardRate = 0.01000;
		double referenceDerivedFXRate = 1. / 1.28;

		double derived3MForwardVolatility = 0.3;
		double derived6MForwardVolatility = 0.3;
		double referenceFundingVolatility = 0.3;
		double reference3MForwardVolatility = 0.3;
		double reference6MForwardVolatility = 0.3;
		double referenceDerivedFXVolatility = 0.3;

		double derived3MForwardFundingCorrelation = 0.15;
		double derived6MForwardFundingCorrelation = 0.15;
		double reference3MForwardFundingCorrelation = 0.15;
		double reference6MForwardFundingCorrelation = 0.15;

		double derived3MForwardFXCorrelation = 0.15;
		double derived6MForwardFXCorrelation = 0.15;
		double reference3MForwardFXCorrelation = 0.15;
		double reference6MForwardFXCorrelation = 0.15;

		double fundingFXCorrelation = 0.15;

		JulianDate today = DateUtil.Today();

		FundingLabel fundingLabelReference = FundingLabel.Standard (referenceCurrency);

		ForwardLabel derived3MForwardLabel = ForwardLabel.Create (derivedCurrency, "3M");

		ForwardLabel derived6MForwardLabel = ForwardLabel.Create (derivedCurrency, "6M");

		ForwardLabel reference3MForwardLabel = ForwardLabel.Create (referenceCurrency, "3M");

		ForwardLabel reference6MForwardLabel = ForwardLabel.Create (referenceCurrency, "6M");

		ValuationParams valuationParams = new ValuationParams (today, today, referenceCurrency);

		CurrencyPair currencyPair = CurrencyPair.FromCode (referenceCurrency + "/" + derivedCurrency);

		FloatFloatComponent nonMTMReferencefloatFloat = MakeFloatFloatSwap (
			today,
			false,
			referenceCurrency,
			referenceCurrency,
			"2Y",
			6,
			3
		);

		nonMTMReferencefloatFloat.setPrimaryCode (
			"FLOAT::FLOAT::" + referenceCurrency + "::" + referenceCurrency + "_3M::" + referenceCurrency +
				"_6M::2Y"
		);

		FloatFloatComponent mtmDerivedFloatFloat = MakeFloatFloatSwap (
			today,
			true,
			referenceCurrency,
			derivedCurrency,
			"2Y",
			6,
			3
		);

		mtmDerivedFloatFloat.setPrimaryCode (
			"FLOAT::FLOAT::MTM::" + referenceCurrency + "::" + derivedCurrency + "_3M::" + derivedCurrency +
				"_6M::2Y"
		);

		FloatFloatComponent floatFloatDerivedNonMTM = MakeFloatFloatSwap (
			today,
			false,
			referenceCurrency,
			derivedCurrency,
			"2Y",
			6,
			3
		);

		floatFloatDerivedNonMTM.setPrimaryCode (
			"FLOAT::FLOAT::NONMTM::" + referenceCurrency + "::" + derivedCurrency + "_3M::" + derivedCurrency
				+ "_6M::2Y"
		);

		int todayJulian = today.julian();

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFixing (today, fxLabel, referenceDerivedFXRate);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				reference3MForwardLabel,
				reference3MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				reference6MForwardLabel,
				reference6MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				derived3MForwardLabel,
				derived3MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (
				today,
				derived6MForwardLabel,
				derived6MForwardRate
			)
		);

		curveSurfaceQuoteContainer.setFundingState (
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (
				today,
				referenceCurrency,
				referenceFundingRate
			)
		);

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				fxLabel.fullyQualifiedName(),
				today,
				currencyPair,
				new String[]
				{
					"10Y"
				},
				new double[]
				{
					referenceDerivedFXRate
				},
				referenceDerivedFXRate
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (reference3MForwardLabel),
				reference3MForwardLabel.currency(),
				reference3MForwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (reference6MForwardLabel),
				reference6MForwardLabel.currency(),
				reference6MForwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (derived3MForwardLabel),
				derived3MForwardLabel.currency(),
				derived3MForwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (derived6MForwardLabel),
				derived6MForwardLabel.currency(),
				derived6MForwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (fundingLabelReference),
				referenceCurrency,
				referenceFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setFXVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				todayJulian,
				VolatilityLabel.Standard (fxLabel),
				derivedCurrency,
				referenceDerivedFXVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			reference3MForwardLabel,
			fundingLabelReference,
			new Flat (reference3MForwardFundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			reference6MForwardLabel,
			fundingLabelReference,
			new Flat (reference6MForwardFundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			derived3MForwardLabel,
			fundingLabelReference,
			new Flat (derived3MForwardFundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			derived6MForwardLabel,
			fundingLabelReference,
			new Flat (derived6MForwardFundingCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			reference3MForwardLabel,
			fxLabel,
			new Flat (reference3MForwardFXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			reference6MForwardLabel,
			fxLabel,
			new Flat (reference6MForwardFXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			derived3MForwardLabel,
			fxLabel,
			new Flat (derived3MForwardFXCorrelation)
		);

		curveSurfaceQuoteContainer.setForwardFXCorrelation (
			derived6MForwardLabel,
			fxLabel,
			new Flat (derived6MForwardFXCorrelation)
		);

		curveSurfaceQuoteContainer.setFundingFXCorrelation (
			fundingLabelReference,
			fxLabel,
			new Flat (fundingFXCorrelation)
		);

		CaseInsensitiveTreeMap<Double> nonMTMMeasureMap = new ComponentPair (
			"FFFF_NonMTM",
			nonMTMReferencefloatFloat,
			floatFloatDerivedNonMTM,
			null
		).value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		for (Map.Entry<String, Double> measureMap : new ComponentPair (
				"FFFF_MTM",
				nonMTMReferencefloatFloat,
				mtmDerivedFloatFloat,
				null
			).value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			).entrySet()
		)
		{
			String key = measureMap.getKey();

			Double value = measureMap.getValue();

			if (null != value && null != nonMTMMeasureMap.get (key)) {
				double mtmMeasure = value;

				double nonMTMMeasure = nonMTMMeasureMap.get (key);

				System.out.println (
					"\t|| " + FormatUtil.FormatDouble (mtmMeasure, 1, 8, 1.) + " | " +
					FormatUtil.FormatDouble (nonMTMMeasure, 1, 8, 1.) + " | " + (
						NumberUtil.WithinTolerance (
							mtmMeasure,
							nonMTMMeasure,
							1.e-08,
							1.e-04
						) ? "RECONCILES" : "DOES NOT RECONCILE"
					) + " <= " + key
				);
			}
		}

		EnvManager.TerminateEnv();
	}
}
