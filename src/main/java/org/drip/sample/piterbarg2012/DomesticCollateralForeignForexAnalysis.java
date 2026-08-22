
package org.drip.sample.piterbarg2012;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
import org.drip.function.r1tor1operator.Flat;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.product.fx.DomesticCollateralizedForeignForward;
import org.drip.product.params.CurrencyPair;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
import org.drip.state.curve.ForeignCollateralizedDiscountCurve;
import org.drip.state.discount.MergedDiscountForwardCurve;
import org.drip.state.fx.FXCurve;
import org.drip.state.identifier.*;
import org.drip.state.nonlinear.*;

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
 * <i>DomesticCollateralForeignForexAnalysis</i> contains an analysis of the correlation and volatility
 * 	impact on the price of a Domestic Collateralized Foreign Pay-out Forex Contract.
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 *  		Barden, P. (2009): Equity Forward Prices in the Presence of Funding Spreads <i>ICBI
 *  			Conference</i> <b>Rome</b>
 *  	</li>
 *  	<li>
 *  		Burgard, C., and M. Kjaer (2009): Modeling and successful Management of Credit Counter-party Risk
 *  			of Derivative Portfolios <i>ICBI Conference</i> <b>Rome</b>
 *  	</li>
 *  	<li>
 *  		Gregory, J. (2009): Being Two-faced over Counter-party Credit Risk <i>Risk</i> <b>20 (2)</b>
 *  			86-90
 *  	</li>
 *  	<li>
 *  		Johannes, M., and S. Sundaresan (2007): Pricing Collateralized Swaps <i>Journal of Finance</i>
 *  			<b>62</b> 383-410
 *  	</li>
 *  	<li>
 *  		Piterbarg, V. (2010): Funding Beyond Discounting: Collateral Agreements and Derivatives Pricing
 *  			<i>Risk</i> <b>21 (2)</b> 97-102
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/TransactionCostAnalyticsLibrary.md">Transaction Cost Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/piterbarg2012/README.md">Piterbarg (2012) Domestic Foreign Collateral</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class DomesticCollateralForeignForexAnalysis
{

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

		String maturity = "1Y";
		String foreignCurrency = "EUR";
		double fxForwardStrike = 0.984;
		String domesticCurrency = "USD";
		double collateralizedFXRate = 1.1;
		double domesticCollateralRate = 0.02;

		double[] foreignRatesVolatilityArray =
		{
			0.1,
			0.2,
			0.3,
			0.4,
			0.5
		};
		double[] fxVolatilityArray =
		{
			0.10,
			0.15,
			0.20,
			0.25,
			0.30
		};
		double[] fxForeignRatesCorrelationArray =
		{
			-0.99,
			-0.50,
			 0.00,
			 0.50,
			 0.99
		};

		int todayJulian = today.julian();

		int[] dateArray = new int[] {todayJulian};

		ValuationParams valuationParams = new ValuationParams (today, today, domesticCurrency);

		CurrencyPair currencyPair = CurrencyPair.FromCode (foreignCurrency + "/" + domesticCurrency);

		MergedDiscountForwardCurve domesticCollateralizedDomesticDiscountCurve =
			ScenarioDiscountCurveBuilder.ExponentiallyCompoundedFlatRate (
				today,
				domesticCurrency,
				domesticCollateralRate
			);

		FXCurve fxCurve = new FlatForwardFXCurve (
			todayJulian,
			currencyPair,
			collateralizedFXRate,
			dateArray,
			new double[] {collateralizedFXRate}
		);

		MergedDiscountForwardCurve foreignCollateralizedDomesticDiscountCurve =
			new ForeignCollateralizedDiscountCurve (
				foreignCurrency,
				domesticCollateralizedDomesticDiscountCurve,
				fxCurve,
				new FlatForwardVolatilityCurve (
					todayJulian,
					VolatilityLabel.Standard (CollateralLabel.Standard (foreignCurrency)),
					domesticCurrency,
					dateArray,
					new double[] {0.}
				),
				new FlatForwardVolatilityCurve (
					todayJulian,
					VolatilityLabel.Standard (FXLabel.Standard (currencyPair)),
					domesticCurrency,
					dateArray,
					new double[] {0.}
				),
				new Flat (
					0.
				)
			);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			null,
			null,
			null,
			null,
			null,
			null,
			null
		);

		curveSurfaceQuoteContainer.setPayCurrencyCollateralCurrencyCurve (
			foreignCurrency,
			domesticCurrency,
			foreignCollateralizedDomesticDiscountCurve
		);

		curveSurfaceQuoteContainer.setPayCurrencyCollateralCurrencyCurve (
			domesticCurrency,
			domesticCurrency,
			domesticCollateralizedDomesticDiscountCurve
		);

		curveSurfaceQuoteContainer.setFXState (
			ScenarioFXCurveBuilder.CubicPolynomialCurve (
				"FX::" + currencyPair.code(),
				today,
				currencyPair,
				new String[] {"10Y"},
				new double[] {collateralizedFXRate},
				collateralizedFXRate
			)
		);

		DomesticCollateralizedForeignForward domesticCollateralizedForeignForward =
			new DomesticCollateralizedForeignForward (
				currencyPair,
				fxForwardStrike,
				today.addTenor (maturity)
			);

		CaseInsensitiveTreeMap<Double> baseValueMap = domesticCollateralizedForeignForward.value (
			new ValuationParams (today, today, domesticCurrency),
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double baselinePrice = baseValueMap.get ("Price");

		double baselineParForward = baseValueMap.get ("ParForward");

		System.out.println ("\t---------------------------------------------------------------------------");

		System.out.println (
			"\tPrinting the Domestic Collateralized Foreign Forex Output in Order (Left -> Right):"
		);

		System.out.println ("\t\tPrice (%)");

		System.out.println ("\t\tPrice Difference (%)");

		System.out.println ("\t\tPar Forward (abs)");

		System.out.println ("\t\tPar Forward Difference (abs)");

		System.out.println ("\t---------------------------------------------------------------------------");

		System.out.println ("\t---------------------------------------------------------------------------");

		for (double foreignRatesVolatility : foreignRatesVolatilityArray) {
			for (double fxVolatility : fxVolatilityArray) {
				for (double fxForeignRatesCorrelation : fxForeignRatesCorrelationArray) {
					foreignCollateralizedDomesticDiscountCurve = new ForeignCollateralizedDiscountCurve (
						foreignCurrency,
						domesticCollateralizedDomesticDiscountCurve,
						fxCurve,
						new FlatForwardVolatilityCurve (
							todayJulian,
							VolatilityLabel.Standard (CollateralLabel.Standard (foreignCurrency)),
							domesticCurrency,
							dateArray,
							new double[] {foreignRatesVolatility}
						),
						new FlatForwardVolatilityCurve (
							todayJulian,
							VolatilityLabel.Standard (FXLabel.Standard (currencyPair)),
							domesticCurrency,
							dateArray,
							new double[] {fxVolatility}
						),
						new Flat (
							fxForeignRatesCorrelation
						)
					);

					curveSurfaceQuoteContainer.setPayCurrencyCollateralCurrencyCurve (
						foreignCurrency,
						domesticCurrency,
						foreignCollateralizedDomesticDiscountCurve
					);

					CaseInsensitiveTreeMap<Double> scenarioValueMap =
						domesticCollateralizedForeignForward.value (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null
						);

					double price = scenarioValueMap.get ("Price");

					double parForward = scenarioValueMap.get ("ParForward");

					System.out.println (
						"\t[" + FormatUtil.FormatDouble (foreignRatesVolatility, 2, 0, 100.) + "%," +
						FormatUtil.FormatDouble (fxVolatility, 2, 0, 100.) + "%," +
						FormatUtil.FormatDouble (fxForeignRatesCorrelation, 2, 0, 100.) + "%] = " +
						FormatUtil.FormatDouble (price, 2, 2, 100.) + " | " +
						FormatUtil.FormatDouble (price - baselinePrice, 2, 2, 100.) + " | " +
						FormatUtil.FormatDouble (parForward, 1, 4, 1.) + " | " +
						FormatUtil.FormatDouble (parForward - baselineParForward, 1, 4, 1.)
					);
				}
			}
		}

		System.out.println ("\t---------------------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
