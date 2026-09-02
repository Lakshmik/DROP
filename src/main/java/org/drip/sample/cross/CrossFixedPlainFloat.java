
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
import org.drip.product.params.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
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
 * <i>CrossFixedPlainFloat</i> demonstrates the construction, usage, and eventual valuation of a fix-float
 * 	swap with a EUR Fixed leg that pays in USD, and a USD Floating Leg. Comparison is done across MTM and
 * 	non-MTM fixed Leg Counterparts.
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

public class CrossFixedPlainFloat
{

	private static final FixFloatComponent MakeFixFloatSwap (
		final JulianDate effectiveDate,
		final boolean fxMTM,
		final String payCurrency,
		final String fixedCouponCurrency,
		final String maturityTenor,
		final int tenorInMonths)
		throws Exception
	{
		String tenor = tenorInMonths + "M";

		return new FixFloatComponent (
			new Stream (
				CompositePeriodBuilder.FixedCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"6M",
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						2,
						"6M",
						payCurrency,
						null,
						1.,
						null,
						null,
						fxMTM ? null : new FixingSetting (
							FixingSetting.FIXING_PRESET_STATIC,
							null,
							effectiveDate.julian()
						),
						null
					),
					new UnitCouponAccrualSetting (
						2,
						"Act/360",
						false,
						"Act/360",
						false,
						fixedCouponCurrency,
						false,
						CompositePeriodBuilder.ACCRUAL_COMPOUNDING_RULE_GEOMETRIC
					),
					new ComposableFixedUnitSetting (
						"6M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						0.02,
						0.,
						fixedCouponCurrency
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						tenor,
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						12 / tenorInMonths,
						tenor,
						payCurrency,
						null,
						-1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						tenor,
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (payCurrency, tenor),
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

		double usd3MForwardRate = 0.02;
		double eurCollateralRate = 0.02;
		double usdCollateralRate = 0.03;
		double usdEURFXRate = 1. / 1.35;

		double usd3MVolatility = 0.1;
		double usdFundingVolatility = 0.1;
		double usd3MUSDFundingCorrelation = 0.1;

		double usdEURFXVolatility = 0.3;
		double eurFundingVolatility = 0.1;
		double usdFundingUSDEURFXCorrelation = 0.3;

		JulianDate today = DateUtil.Today();

		FundingLabel usdFundingLabel = FundingLabel.Standard ("USD");

		CurrencyPair currencyPair = CurrencyPair.FromCode ("USD/EUR");

		ForwardLabel usd3MForwardLabel = ForwardLabel.Create ("USD", "3M");

		ValuationParams valuationParams = new ValuationParams (today, today, "USD");

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		FXLabel fxLabel = FXLabel.Standard (currencyPair);

		curveSurfaceQuoteContainer.setFundingState (
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (today, "USD", usdCollateralRate)
		);

		curveSurfaceQuoteContainer.setForwardState (
			ScenarioForwardCurveBuilder.FlatForwardForwardCurve (today, usd3MForwardLabel, usd3MForwardRate)
		);

		int valuationDate = valuationParams.valueDate();

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (usd3MForwardLabel),
				"USD",
				usd3MVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (usdFundingLabel),
				"USD",
				usdFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			usd3MForwardLabel,
			usdFundingLabel,
			new Flat (usd3MUSDFundingCorrelation)
		);

		curveSurfaceQuoteContainer.setFundingState (
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (today, "EUR", eurCollateralRate)
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
					usdEURFXRate
				},
				usdEURFXRate
			)
		);

		curveSurfaceQuoteContainer.setFixing (today, fxLabel, usdEURFXRate);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (FundingLabel.Standard ("EUR")),
				"EUR",
				eurFundingVolatility
			)
		);

		curveSurfaceQuoteContainer.setFXVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (fxLabel),
				"USD",
				usdEURFXVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingFXCorrelation (
			usdFundingLabel,
			fxLabel,
			new Flat (usdFundingUSDEURFXCorrelation)
		);

		CaseInsensitiveTreeMap<Double> nonMTMFixFloatMeasureMap = MakeFixFloatSwap (
			today,
			false,
			"USD",
			"EUR",
			"2Y",
			3
		).value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		for (Map.Entry<String, Double> measureMap : MakeFixFloatSwap (
				today,
				true,
				"USD",
				"EUR",
				"2Y",
				3
			).value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			).entrySet()
		)
		{
			String key = measureMap.getKey();

			if (null != measureMap.getValue() && null != nonMTMFixFloatMeasureMap.get (key)) {
				double mtmMeasure = measureMap.getValue();

				double nonMTMMeasure = nonMTMFixFloatMeasureMap.get (key);

				System.out.println (
					"\t|| " + FormatUtil.FormatDouble (mtmMeasure, 1, 8, 1.) + " | " +
					FormatUtil.FormatDouble (nonMTMMeasure, 1, 8, 1.) + " | " + (
						NumberUtil.WithinTolerance (mtmMeasure, nonMTMMeasure, 1.e-08, 1.e-04) ?
							"RECONCILES" : "DOES NOT RECONCILE"
					) + " <= " + key
				);
			}
		}

		EnvManager.TerminateEnv();
	}
}
