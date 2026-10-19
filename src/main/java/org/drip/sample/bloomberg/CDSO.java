
package org.drip.sample.bloomberg;

import java.util.Map;

import org.drip.analytics.date.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.pricer.CreditPricerParams;
import org.drip.param.valuation.ValuationParams;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.CDSBuilder;
import org.drip.product.definition.CreditDefaultSwap;
import org.drip.product.option.CDSEuropeanOption;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.service.template.LatentMarketStateBuilder;
import org.drip.state.creator.ScenarioDeterministicVolatilityBuilder;
import org.drip.state.credit.CreditCurve;
import org.drip.state.discount.MergedDiscountForwardCurve;
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
 * <i>CDSO</i> contains the sample demonstrating the replication of Bloomberg's CDSO functionality.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/bloomberg/README.md">Bloomberg CDSO CDSW SWPM YAS</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class CDSO
{

	private static final MergedDiscountForwardCurve MarketFundingCurve (
		final JulianDate spotDate,
		final String currency,
		final double bump)
	{
		return LatentMarketStateBuilder.SmoothFundingCurve (
			spotDate,
			currency,
			new String[]
			{
				"02D",
				"07D",
				"14D",
				"30D",
				"60D"
			},
			new double[]
			{
				0.0017 + bump,	//  2D
				0.0017 + bump,	//  7D
				0.0018 + bump,	// 14D
				0.0020 + bump,	// 30D
				0.0023 + bump	// 60D
			},
			"ForwardRate",
			new double[]
			{
				0.0027 + bump,
				0.0032 + bump,
				0.0041 + bump,
				0.0054 + bump,
				0.0077 + bump,
				0.0104 + bump,
				0.0134 + bump,
				0.0160 + bump
			},
			"ForwardRate",
			new String[]
			{
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
			},
			new double[]
			{
				0.0166 + bump,	//  4Y
				0.0206 + bump,	//  5Y
				0.0241 + bump,	//  6Y
				0.0269 + bump,	//  7Y
				0.0292 + bump,	//  8Y
				0.0311 + bump,	//  9Y
				0.0326 + bump,	// 10Y
				0.0340 + bump,	// 11Y
				0.0351 + bump,	// 12Y
				0.0375 + bump,	// 15Y
				0.0393 + bump,	// 20Y
				0.0402 + bump,	// 25Y
				0.0407 + bump,	// 30Y
				0.0409 + bump,	// 40Y
				0.0409 + bump	// 50Y
			},
			"SwapRate"
		);
	}

	private static final CreditCurve MarketCreditCurve (
		final JulianDate spotDate,
		final String creditCurveName,
		final String manifestMeasure,
		final MergedDiscountForwardCurve fundingCurve,
		final double bump,
		final boolean display)
		throws Exception
	{
		String[] cdsMaturityTenorArray =
		{
			"06M",
			"01Y",
			"02Y",
			"03Y",
			"04Y",
			"05Y",
			"07Y",
			"10Y"
		};
		double[] cdsParSpreadArray =
		{
			100. + bump,	//  6M
			100. + bump,	//  1Y
			100. + bump,	//  2Y
			100. + bump,	//  3Y
			100. + bump,	//  4Y
			100. + bump,	//  5Y
			100. + bump,	//  7Y
			100. + bump	// 10Y
		};

		CreditCurve creditCurve = LatentMarketStateBuilder.CreditCurve (
			spotDate,
			creditCurveName,
			cdsMaturityTenorArray,
			cdsParSpreadArray,
			cdsParSpreadArray,
			manifestMeasure,
			fundingCurve
		);

		if (!display) {
			return creditCurve;
		}

		CreditPricerParams pricerParams = CreditPricerParams.Standard();

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFundingState (fundingCurve);

		curveSurfaceQuoteContainer.setCreditState (creditCurve);

		System.out.println ("\n\t|---------------||");

		System.out.println ("\t| CREDIT SPREAD ||");

		System.out.println ("\t|---------------||");

		for (int cdsIndex = 0; cdsIndex < cdsParSpreadArray.length; ++cdsIndex) {
			System.out.println (
				"\t| " + cdsMaturityTenorArray[cdsIndex] + " |" + FormatUtil.FormatDouble (
					CDSBuilder.CreateSNAC (
						spotDate,
						cdsMaturityTenorArray[cdsIndex],
						0.1,
						creditCurveName
					).measureValue (
						ValuationParams.Spot (spotDate.julian()),
						pricerParams,
						curveSurfaceQuoteContainer,
						null,
						manifestMeasure
					),
					3,
					3,
					1.
				) + " ||"
			);
		}

		System.out.println ("\t|---------------||");

		return creditCurve;
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
		EnvManager.InitEnv ("", true);

		JulianDate spotDate = DateUtil.CreateFromYMD (2016, DateUtil.FEBRUARY, 2);

		JulianDate cashPayDate = DateUtil.CreateFromYMD (2016, DateUtil.FEBRUARY, 5);

		double creditBump = 10.;
		String currency = "USD";
		double fundingBump = .0001;
		double notional = 10000000.;
		double cdsForwardCoupon = 0.1;
		String cdsForwardTenor = "5Y";
		String creditCurveName = "DB";
		double fairPremiumVolatility = 0.3;
		String manifestMeasure = "FairPremium";

		MergedDiscountForwardCurve fundingCurve = MarketFundingCurve (spotDate, currency, 0.);

		CreditCurve creditCurve = MarketCreditCurve (
			spotDate,
			creditCurveName,
			manifestMeasure,
			fundingCurve,
			0.,
			true
		);

		CreditPricerParams pricerParams = CreditPricerParams.Standard();

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setFundingState (fundingCurve);

		curveSurfaceQuoteContainer.setCreditState (creditCurve);

		JulianDate effectiveDate = DateUtil.CreateFromYMD (
			2016,
			DateUtil.MARCH,
			20
		);

		CreditDefaultSwap cdsForward = CDSBuilder.CreateSNAC (
			effectiveDate,
			cdsForwardTenor,
			cdsForwardCoupon,
			creditCurveName
		);

		System.out.println ("\n\t|-------------------------------------|");

		System.out.println ("\t|        UNDERLYING CDS FORWARD       |");

		System.out.println ("\t|-------------------------------------|");

		System.out.println ("\t|  Effective Date    : " + cdsForward.effectiveDate());

		System.out.println ("\t|  Maturity Date     : " + cdsForward.maturityDate());

		System.out.println ("\t|  Notional          : " + FormatUtil.FormatDouble (notional, 1, 0, 1.));

		System.out.println ("\t|  Payment Frequency : " + cdsForward.freq());

		System.out.println ("\t|  Currency          : " + cdsForward.payCurrency());

		System.out.println ("\t|-------------------------------------|\n");

		curveSurfaceQuoteContainer.setCustomVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				spotDate.julian(),
				VolatilityLabel.Standard (CustomLabel.Standard (cdsForward.name() + "_" + manifestMeasure)),
				currency,
				fairPremiumVolatility
			)
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, cashPayDate, currency);

		double strike = cdsForward.value (
			valuationParams,
			pricerParams,
			curveSurfaceQuoteContainer,
			null
		).get (
			manifestMeasure
		);

		CDSEuropeanOption cdsOptionPayer = new CDSEuropeanOption (
			cdsForward.name() + "::PAYER_OPT",
			cdsForward,
			manifestMeasure,
			false,
			strike,
			null,
			new BlackScholesAlgorithm(),
			null
		);

		System.out.println ("\n\t|-------------------------------------|");

		System.out.println ("\t|          OPTION PARAMETERS          |");

		System.out.println ("\t|-------------------------------------|");

		System.out.println ("\t| Payer Swaption?  " + true);

		System.out.println ("\t| Exercise Type  : " + "European");

		System.out.println ("\t| Knock Out?       " + true);

		System.out.println ("\t| Strike         : " + FormatUtil.FormatDouble (strike, 1, 0, 1.));

		System.out.println ("\t| Start Date     : " + spotDate);

		System.out.println ("\t| Cash Pay Date  : " + cashPayDate);

		System.out.println ("\t| Exercise Date  : " + cdsOptionPayer.exerciseDate());

		System.out.println ("\t|-------------------------------------|\n");

		Map<String, Double> payerOptionMeasureMap = cdsOptionPayer.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double optionPriceBase = payerOptionMeasureMap.get ("Price") / cdsForward.initialNotional();

		CreditCurve creditCurveBumpUp = MarketCreditCurve (
			spotDate,
			creditCurveName,
			manifestMeasure,
			fundingCurve,
			creditBump,
			false
		);

		CreditCurve creditCurveBumpDown = MarketCreditCurve (
			spotDate,
			creditCurveName,
			manifestMeasure,
			fundingCurve,
			-creditBump,
			false
		);

		curveSurfaceQuoteContainer.setCreditState (creditCurveBumpUp);

		Map<String, Double> payerOptionMeasureMapCreditBumpUp = cdsOptionPayer.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double optionPriceCreditBumpUp =
			payerOptionMeasureMapCreditBumpUp.get ("Price") / cdsForward.initialNotional();

		curveSurfaceQuoteContainer.setCreditState (creditCurveBumpDown);

		Map<String, Double> payerOptionMeasureMapCreditBumpDown = cdsOptionPayer.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double optionPriceCreditBumpDown =
			payerOptionMeasureMapCreditBumpDown.get ("Price") / cdsForward.initialNotional();

		MergedDiscountForwardCurve fundingCurveBumpUp = MarketFundingCurve (spotDate,
			currency,
			fundingBump
		);

		curveSurfaceQuoteContainer.setFundingState (fundingCurveBumpUp);

		curveSurfaceQuoteContainer.setCreditState (creditCurve);

		Map<String, Double> payerOptionMeasureMapFundingBumpUp = cdsOptionPayer.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double optionPriceFundingBumpUp =
			payerOptionMeasureMapFundingBumpUp.get ("Price") / cdsForward.initialNotional();

		MergedDiscountForwardCurve fundingCurveBumpDown = MarketFundingCurve (
			spotDate,
			currency,
			-fundingBump
		);

		curveSurfaceQuoteContainer.setFundingState (fundingCurveBumpDown);

		Map<String, Double> payerOptionMeasureMapFundingBumpDown = cdsOptionPayer.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		);

		double optionPriceFundingBumpDown =
			payerOptionMeasureMapFundingBumpDown.get ("Price") / cdsForward.initialNotional();

		System.out.println ("\n\t|-----------------------------------------------|");

		System.out.println ("\t|           OPTION INPUTS AND PRICING           |");

		System.out.println ("\t|-----------------------------------------------|");

		System.out.println ("\t| Valuation Date          : " + spotDate);

		System.out.println (
			"\t| Fair Premium Volatility : " + FormatUtil.FormatDouble (fairPremiumVolatility, 1, 3, 100.) +
				"%"
		);

		System.out.println (
			"\t| Option Premium          : " + FormatUtil.FormatDouble (optionPriceBase, 1, 5, 100.) + "%"
		);

		System.out.println (
			"\t| Option MTM              : " + FormatUtil.FormatDouble (optionPriceBase * notional, 1, 2, 1.)
		);

		System.out.println (
			"\t| Credit Spread Delta 01  : " + FormatUtil.FormatDouble (
				0.5 * (optionPriceCreditBumpUp - optionPriceCreditBumpDown) * notional / creditBump,
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t| Credit Spread Gamma 01  : " + FormatUtil.FormatDouble (
				0.5 * (optionPriceCreditBumpUp + optionPriceCreditBumpDown - 2. * optionPriceBase) * notional
					/ (creditBump * creditBump),
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t| Funding Spread Delta 01 : " + FormatUtil.FormatDouble (
				0.5 * (optionPriceFundingBumpUp - optionPriceFundingBumpDown) * notional /
					(10000. * fundingBump),
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t| Funding Spread Gamma 01 : " + FormatUtil.FormatDouble (
				0.5 * (
					optionPriceFundingBumpUp + optionPriceFundingBumpDown - 2. * optionPriceBase
				) * notional / (
					10000. * 10000. * fundingBump * fundingBump
				),
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t| ATM Forward             : " + FormatUtil.FormatDouble (
				payerOptionMeasureMap.get ("ATMManifestMeasure"),
				1,
				0,
				1.
			)
		);

		System.out.println (
			"\t| FPG Delta               : " +
				FormatUtil.FormatDouble (payerOptionMeasureMap.get ("FPGDelta"), 1, 4, 1.)
		);

		System.out.println (
			"\t| FPG Gamma               : " +
				FormatUtil.FormatDouble (payerOptionMeasureMap.get ("FPGGamma"), 1, 4, 1.)
		);

		System.out.println (
			"\t| FPG Vega                : " +
				FormatUtil.FormatDouble (payerOptionMeasureMap.get ("FPGVega"), 1, 2, 1.)
		);

		System.out.println (
			"\t| FPG Theta               : " +
				FormatUtil.FormatDouble (payerOptionMeasureMap.get ("FPGTheta"), 1, 2, 1.)
		);

		System.out.println (
			"\t| Vega (1%)               : " + FormatUtil.FormatDouble (
				payerOptionMeasureMap.get ("Vega") / cdsForward.initialNotional() * notional * 0.01,
				1,
				2,
				1.
			)
		);

		System.out.println (
			"\t| Theta                   : " + FormatUtil.FormatDouble (
				payerOptionMeasureMap.get ("Theta") / cdsForward.initialNotional() * notional / 365.25,
				1,
				2,
				1.
			)
		);

		System.out.println ("\t|-----------------------------------------------|\n");

		EnvManager.TerminateEnv();
	}
}
