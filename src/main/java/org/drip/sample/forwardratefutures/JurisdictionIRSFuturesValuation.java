
package org.drip.sample.forwardratefutures;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.exchange.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.estimator.LatentStateStretchBuilder;
import org.drip.state.identifier.*;
import org.drip.state.inference.*;

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
 * <i>JurisdictionIRSFuturesValuation</i> contains the demonstration of the construction and the Valuation of
 * 	the Exchange-Traded IRS Futures Contract.
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

public class JurisdictionIRSFuturesValuation
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

	private static final SingleStreamComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final String currency,
		final int[] maturityDaysArray)
		throws Exception
	{
		SingleStreamComponent[] depositComponentArray = new SingleStreamComponent[maturityDaysArray.length];

		ComposableFloatingUnitSetting composableFloatingUnitSetting = new ComposableFloatingUnitSetting (
			"3M",
			CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
			null,
			ForwardLabel.Create (currency, "3M"
			),
			CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
			0.
		);

		CompositePeriodSetting compositePeriodSetting = new CompositePeriodSetting (
			4,
			"3M",
			currency,
			null,
			1.,
			null,
			null,
			null,
			null
		);

		CashSettleParams cashSettleParams = new CashSettleParams (0, currency, 0);

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositComponentArray[maturityIndex] = new SingleStreamComponent (
				"DEPOSIT_" + maturityDaysArray[maturityIndex],
				new Stream (
					CompositePeriodBuilder.FloatingCompositeUnit (
						CompositePeriodBuilder.EdgePair (
							effectiveDate,
							effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency)
						),
						compositePeriodSetting,
						composableFloatingUnitSetting
					)
				),
				cashSettleParams
			);

			depositComponentArray[maturityIndex].setPrimaryCode (maturityDaysArray[maturityIndex] + "D");
		}

		return depositComponentArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			irsArray[maturityIndex] = OTCIRS (spotDate, currency, maturityTenorArray[maturityIndex], 0.);
		}

		return irsArray;
	}

	private static final void OTCInstrumentCurve (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		SingleStreamComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[]
			{
				1,
				2,
				7,
				14,
				30,
				60
			}
		);

		double[] depositQuoteArray =
		{
			0.0013,
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023
		};

		SingleStreamComponent[] edfComponentArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			spotDate,
			8,
			currency
		);

		double[] edfQuoteArray =
		{
			0.0027,
			0.0032,
			0.0041,
			0.0054,
			0.0077,
			0.0104,
			0.0134,
			0.0160
		};

		FixFloatComponent[] swapComponentArray = SwapInstrumentsFromMaturityTenor (
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
			}
		);

		double[] swapQuoteArray =
		{
			0.0166,
			0.0206,
			0.0241,
			0.0269,
			0.0292,
			0.0311,
			0.0326,
			0.0340,
			0.0351,
			0.0375,
			0.0393,
			0.0402,
			0.0407,
			0.0409,
			0.0409
		};

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
				currency,
				new LinearLatentStateCalibrator (
					new SegmentCustomBuilderControl (
						MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
						new PolynomialFunctionSetParams (4),
						SegmentInelasticDesignControl.Create (2, 2),
						new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
						null
					),
					BoundarySettings.NaturalStandard(),
					MultiSegmentSequence.CALIBRATE,
					null,
					null
				),
				new LatentStateStretchSpec[]
				{
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"DEPOSIT",
						depositComponentArray,
						"ForwardRate",
						depositQuoteArray
					),
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"EDF",
						edfComponentArray,
						"ForwardRate",
						edfQuoteArray
					),
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"SWAP",
						swapComponentArray,
						"SwapRate",
						swapQuoteArray
					)
				},
				valuationParams,
				null,
				null,
				null,
				1.
			),
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositComponentArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositComponentArray[depositIndex].maturityDate() + "] = " +
					FormatUtil.FormatDouble (
						depositComponentArray[depositIndex].measureValue (
							valuationParams,
							null,
							curveSurfaceQuoteContainer,
							null,
							"Rate"
						),
						1,
						6,
						1.
					) + " | " + FormatUtil.FormatDouble (
						depositQuoteArray[depositIndex],
						1,
						6,
						1.
					)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     EDF INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int edfIndex = 0; edfIndex < edfComponentArray.length; ++edfIndex) {
			System.out.println (
				"\t|| [" + edfComponentArray[edfIndex].maturityDate() + "] = " + FormatUtil.FormatDouble (
					edfComponentArray[edfIndex].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					edfQuoteArray[edfIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||     SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int i = 0; i < swapComponentArray.length; ++i) {
			System.out.println (
				"\t|| [" + swapComponentArray[i].maturityDate() + "] = " + FormatUtil.FormatDouble (
					swapComponentArray[i].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					swapQuoteArray[i],
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					swapComponentArray[i].measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"FairPremium"
					),
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||     EXCHANGE-TRADED SWAP INSTRUMENTS VALUATION");

		System.out.println ("\t||----------------------------------------------------------------");

		String[] exchangeTenorArray =
		{
			"2Y",
			"5Y",
			"10Y",
			"30Y"
		};

		double[] couponArray =
		{
			0.0075,
			0.0200,
			0.0325,
			0.0400
		};

		for (int exchangeIRSIndex = 0; exchangeIRSIndex < exchangeTenorArray.length; ++exchangeIRSIndex) {
			FixFloatComponent exchangeTradedIRS = DeliverableSwapFuturesContainer.ProductInfo (
				currency,
				exchangeTenorArray[exchangeIRSIndex]
			).Create (spotDate, couponArray[exchangeIRSIndex]);

			System.out.println (
				"\t|| [" + exchangeTradedIRS.maturityDate() + "] = " + FormatUtil.FormatDouble (
					exchangeTradedIRS.measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					exchangeTradedIRS.measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"FairPremium"
					),
					1,
					6,
					1.
				) + " | " + FormatUtil.FormatDouble (
					exchangeTradedIRS.measureValue (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						"PV"
					),
					4,
					0,
					1.
				) + " | " + exchangeTenorArray[exchangeIRSIndex]
			);
		}
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

		OTCInstrumentCurve (DateUtil.Today().addTenor ("0D"), "USD");

		EnvManager.TerminateEnv();
	}
}
