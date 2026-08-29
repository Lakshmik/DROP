
package org.drip.sample.fra;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.param.creator.MarketParamsBuilder;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.fra.FRAMarketComponent;
import org.drip.sample.forward.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.discount.*;
import org.drip.state.forward.ForwardCurve;
import org.drip.state.identifier.*;
import org.drip.state.nonlinear.FlatForwardVolatilityCurve;
import org.drip.state.volatility.VolatilityCurve;

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
 * <i>MultiCurveFRAMarketAnalysis</i> contains an analysis of the correlation and volatility impact on the
 * 	Market FRA.
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

public class MultiCurveFRAMarketAnalysis
{

	static class FRAMktConvexityCorrection
	{
		double _parMarketForward = Double.NaN;
		double _parStandardForward = Double.NaN;
		double _convexityCorrection = Double.NaN;

		FRAMktConvexityCorrection (
			final double parMarketForward,
			final double parStandardForward,
			final double convexityCorrection)
		{
			_parMarketForward = parMarketForward;
			_parStandardForward = parStandardForward;
			_convexityCorrection = convexityCorrection;
		}
	}

	private static final VolatilityCurve ATMVolatilityCurve (
		final JulianDate epochDate,
		final VolatilityLabel label,
		final String currency,
		final String[] tenorArray,
		final double[] volatilityArray)
		throws Exception
	{
		int[] pillarDateArray = new int[tenorArray.length];

		for (int pillarIndex = 0; pillarIndex < pillarDateArray.length; ++pillarIndex) {
			pillarDateArray[pillarIndex] = epochDate.addTenor (tenorArray[pillarIndex]).julian();
		}

		return new FlatForwardVolatilityCurve (
			epochDate.julian(),
			label,
			currency,
			pillarDateArray,
			volatilityArray
		);
	}

	private static final FRAMktConvexityCorrection FRAMktMetric (
		final JulianDate valueDate,
		final MergedDiscountForwardCurve eoniaDiscountCurve,
		final ForwardCurve euribor6MForwardCurve,
		final String forwardStartTenor,
		final VolatilityCurve eoniaVolatilityCurve,
		final VolatilityCurve euribor6MVolatilityCurve,
		final double eoniaEURIBOR6MCorrelation)
		throws Exception
	{
		String tenor = "6M";
		String currency = "USD";

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		FRAMarketComponent fra = SingleStreamComponentBuilder.FRAMarket (
			valueDate.addTenor (forwardStartTenor),
			forwardLabel,
			0.006
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			eoniaDiscountCurve,
			euribor6MForwardCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		curveSurfaceQuoteContainer.setForwardVolatility (euribor6MVolatilityCurve);

		curveSurfaceQuoteContainer.setFundingVolatility (eoniaVolatilityCurve);

		curveSurfaceQuoteContainer.setForwardFundingCorrelation (
			forwardLabel,
			fundingLabel,
			new Flat (eoniaEURIBOR6MCorrelation)
		);

		Map<String, Double> fraMeasureMap = fra.value (
			new ValuationParams (valueDate, valueDate, currency),
			null,
			curveSurfaceQuoteContainer,
			null
		);

		return new FRAMktConvexityCorrection (
			fraMeasureMap.get ("shiftedlognormalparmarketfra"),
			fraMeasureMap.get ("parstandardfra"),
			fraMeasureMap.get ("shiftedlognormalconvexitycorrection")
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

		String tenor = "6M";
		String currency = "USD";
		String[] forwardStartTenorArray =
		{
			"6M",
			"1Y",
			"2Y",
			"3Y",
			"4Y",
			"5Y",
			"6Y",
			"7Y",
			"8Y",
			"9Y"
		};
		double[] volatilityArray =
		{
			0.5946, // 6M
			0.5311,	// 1Y
			0.3307,	// 2Y
			0.2929,	// 3Y
			0.2433,	// 4Y
			0.2013,	// 5Y
			0.1855,	// 6Y
			0.1789,	// 7Y
			0.1655,	// 8Y
			0.1574	// 9Y
		};
		double eoniaEURIBOR6MCorrelation = 0.8;

		JulianDate today = DateUtil.Today().addTenor ("0D");

		MergedDiscountForwardCurve eoniaDiscountCurve = OvernightIndexCurve.MakeDC (today, currency);

		ForwardCurve euribor6MForwardCurve = IBOR6MQuarticPolyVanilla.Make6MForward (today, currency, tenor);

		VolatilityCurve eoniaVolatilityCurve = ATMVolatilityCurve (
			today,
			VolatilityLabel.Standard (FundingLabel.Standard (currency)),
			currency,
			forwardStartTenorArray,
			volatilityArray
		);

		VolatilityCurve eurbor6MVolatilityCurve = ATMVolatilityCurve (
			today,
			VolatilityLabel.Standard (ForwardLabel.Create (currency, tenor)),
			currency,
			forwardStartTenorArray,
			volatilityArray
		);

		System.out.println ("\t||----------------------------------|");

		System.out.println ("\t||----------------------------------|");

		System.out.println ("\t|| TNR =>   MKT  |   STD  |  CONV   |");

		System.out.println ("\t||----------------------------------|");

		for (String forwardStartTenor : forwardStartTenorArray) {
			FRAMktConvexityCorrection fraMktMetric = FRAMktMetric (
				today,
				eoniaDiscountCurve,
				euribor6MForwardCurve,
				forwardStartTenor,
				eoniaVolatilityCurve,
				eurbor6MVolatilityCurve,
				eoniaEURIBOR6MCorrelation
			);

			System.out.println (
				"\t|| " + forwardStartTenor + " => " +
				FormatUtil.FormatDouble (fraMktMetric._parMarketForward, 1, 3, 100.) + "% | " +
				FormatUtil.FormatDouble (fraMktMetric._parStandardForward, 1, 3, 100.) + "% | " +
				FormatUtil.FormatDouble (fraMktMetric._convexityCorrection, 1, 2, 10000.)
			);
		}

		System.out.println ("\t||----------------------------------|");

		EnvManager.TerminateEnv();
	}
}
