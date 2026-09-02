
package org.drip.sample.cms;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.*;
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
 * <i>FloatFloatVarianceAnalysis</i> demonstrates the Construction and Valuation Impact of Volatility and
 * 	Correlation on the CMS Float-Float Swap.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/cms/README.md">Dual Stream Constant Maturity Swap</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FloatFloatVarianceAnalysis
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
			irsArray[irsIndex] = OTCIRS (
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

	private static final FloatFloatComponent MakeFloatFloatSwap (
		final JulianDate effectiveDate,
		final String currency,
		final String floaterTenor,
		final String maturityTenor,
		final boolean inArrears)
		throws Exception
	{
		return new FloatFloatComponent (
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"3M",
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						4,
						"3M",
						currency,
						null,
						1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						"3M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (currency, "3M"),
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate,
						"3M",
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						4,
						"3M",
						currency,
						null,
						-1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						"3M",
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_REGULAR,
						null,
						ForwardLabel.Create (currency, floaterTenor),
						inArrears ?
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ARREARS :
							CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			new CashSettleParams (0, currency, 0)
		);
	}

	private static final void SetMarketParams (
		final int valuationDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel,
		final FundingLabel fundingLabel,
		final double fundingVolatility,
		final double forwardVolatility,
		final double forwardFundingCorrelation)
		throws Exception
	{
		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
				VolatilityLabel.Standard (forwardLabel),
				forwardLabel.currency(),
				forwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valuationDate,
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

	private static final void VolCorrScenario (
		final FloatFloatComponent[] cmsArray,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel,
		final FundingLabel fundingLabel,
		final double forwardVolatility,
		final double fundingVolatility,
		final double forwardFundingCorrelation,
		final double baseReferenceParBasisSpread)
		throws Exception
	{
		SetMarketParams (
			valuationParams.valueDate(),
			curveSurfaceQuoteContainer,
			forwardLabel,
			fundingLabel,
			forwardVolatility,
			fundingVolatility,
			forwardFundingCorrelation
		);

		String dump = "\t[" + FormatUtil.FormatDouble (forwardVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (fundingVolatility, 2, 0, 100.) + "%," +
			FormatUtil.FormatDouble (forwardFundingCorrelation, 2, 0, 100.) + "%] = ";

		for (int cmsIndex = 0; cmsIndex < cmsArray.length; ++cmsIndex) {
			CaseInsensitiveTreeMap<Double> mapOutput = cmsArray[cmsIndex].value (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null
			);

			double referenceParBasisSpread = mapOutput.get ("ReferenceParBasisSpread");

			dump += (0 != cmsIndex ? " || " : "") +
				FormatUtil.FormatDouble (referenceParBasisSpread, 2, 1, 1.) + " | " +
				FormatUtil.FormatDouble (referenceParBasisSpread - baseReferenceParBasisSpread, 2, 1, 1.);
		}

		System.out.println (dump + "  |");
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
		String maturityTenor = "5Y";
		double[] forwardVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] fundingVolatilityArray =
		{
			0.1,
			0.3,
			0.5
		};
		double[] forwardFundingCorrelationArray =
		{
			-0.10,
			 0.25
		};

		JulianDate spotDate = DateUtil.CreateFromYMD (2012, DateUtil.DECEMBER, 11);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		curveSurfaceQuoteContainer.setFundingState (MakeDC (spotDate, currency));

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, tenor);

		FundingLabel fundingLabel = FundingLabel.Standard (currency);

		FloatFloatComponent cmsInAdvance = MakeFloatFloatSwap (
			spotDate,
			currency,
			tenor,
			maturityTenor,
			false
		);

		FloatFloatComponent cmsInArrears = MakeFloatFloatSwap (
			spotDate,
			currency,
			tenor,
			maturityTenor,
			true
		);

		double baseReferenceParBasisSpread = cmsInAdvance.value (
			valuationParams,
			null,
			curveSurfaceQuoteContainer,
			null
		).get (
			"ReferenceParBasisSpread"
		);

		System.out.println ("\n\n\t|--------------------------------------------------|");

		System.out.println ("\t| CMS FLOAT-FLOAT IN-ADVANCE & IN-ARREARS ANALYSIS |");

		System.out.println ("\t|--------------------------------------------------|");

		System.out.println ("\t| INPUTS: L -> R                                   |");

		System.out.println ("\t|                                                  |");

		System.out.println ("\t|   Forward State Volatility                       |");

		System.out.println ("\t|   Funding State Volatility                       |");

		System.out.println ("\t|   Forward-Funding Correlation                    |");

		System.out.println ("\t|                                                  |");

		System.out.println ("\t|--------------------------------------------------|");

		System.out.println ("\t| OUTPUTS: L -> R                                  |");

		System.out.println ("\t|                                                  |");

		System.out.println ("\t|   In Advance Reference Par Basis Spread          |");

		System.out.println ("\t|   In Advance Reference Par Basis Spread Change   |");

		System.out.println ("\t|   In Arrears Reference Par Basis Spread          |");

		System.out.println ("\t|   In Arrears Reference Par Basis Spread Change   |");

		System.out.println ("\t|                                                  |");

		System.out.println ("\t|--------------------------------------------------|");

		for (double forwardVolatility : forwardVolatilityArray) {
			for (double fundingVolatility : fundingVolatilityArray) {
				for (double forwardFundingCorrelation : forwardFundingCorrelationArray) {
					VolCorrScenario (
						new FloatFloatComponent[] {
							cmsInAdvance,
							cmsInArrears
						},
						valuationParams,
						curveSurfaceQuoteContainer,
						forwardLabel,
						fundingLabel,
						forwardVolatility,
						fundingVolatility,
						forwardFundingCorrelation,
						baseReferenceParBasisSpread
					);
				}
			}
		}

		System.out.println ("\t|--------------------------------------------------|");

		EnvManager.TerminateEnv();
	}
}
