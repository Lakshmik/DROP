
package org.drip.sample.capfloor;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.CompositePeriodBuilder;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.ValuationParams;
import org.drip.pricer.option.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.fra.FRAStandardCapFloor;
import org.drip.product.params.LastTradingDateSetting;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.*;
import org.drip.state.identifier.ForwardLabel;

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
 * <i>FRAStdCapModels</i> runs a side-by-side comparison of the FRA Cap sequence using different models.
 * 
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics
 * 				<i>Mathematical Finance</i> <b>7 (2)</b> 127-155
 *  	</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/capfloor/README.md">FRA Standard Cap Floor Valuation</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class FRAStdCapModels
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

	private static final CalibratableComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final int[] daysArray,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibratableComponentArray =
			new CalibratableComponent[daysArray.length + futuresCount];

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, "3M");

		for (int daysIndex = 0; daysIndex < daysArray.length; ++daysIndex) {
			calibratableComponentArray[daysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (daysArray[daysIndex], currency),
				forwardLabel
			);
		}

		CalibratableComponent[] edfComponentArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int daysIndex = daysArray.length; daysIndex < daysArray.length + futuresCount; ++daysIndex) {
			calibratableComponentArray[daysIndex] = edfComponentArray[daysIndex - daysArray.length];
		}

		return calibratableComponentArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final double[] couponArray)
		throws Exception
	{
		FixFloatComponent[] irsFixFloatComponentArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			irsFixFloatComponentArray[maturityTenorIndex] = OTCFixFloat (
				spotDate,
				currency,
				maturityTenorArray[maturityTenorIndex],
				couponArray[maturityTenorIndex]
			);
		}

		return irsFixFloatComponentArray;
	}

	private static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		double[] irsFixFloatComponentQuoteArray =
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
				irsFixFloatComponentQuoteArray
			),
			irsFixFloatComponentQuoteArray,
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

	private static final FRAStandardCapFloor MakeCap (
		final JulianDate effectiveDate,
		final ForwardLabel forwardLabel,
		final String maturityTenor,
		final String manifestMeasure,
		final double strike,
		final FokkerPlanckGenerator fokkerPlanckGenerator)
		throws Exception
	{
		return new FRAStandardCapFloor (
			"FRA_CAP",
			new Stream (
				CompositePeriodBuilder.FloatingCompositeUnit (
					CompositePeriodBuilder.RegularEdgeDates (
						effectiveDate.julian(),
						forwardLabel.tenor(),
						maturityTenor,
						null
					),
					new CompositePeriodSetting (
						4,
						forwardLabel.tenor(),
						forwardLabel.currency(),
						null,
						1.,
						null,
						null,
						null,
						null
					),
					new ComposableFloatingUnitSetting (
						forwardLabel.tenor(),
						CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
						null,
						forwardLabel,
						CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
						0.
					)
				)
			),
			manifestMeasure,
			true,
			strike,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			null,
			fokkerPlanckGenerator
		);
	}

	private static final Map<JulianDate, Double> ValueCap (
		final ForwardLabel forwardLabel,
		final String manifestMeasure,
		final ValuationParams valuationParams,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final String[] maturityTenorArray,
		final double[] atmStrikeArray,
		final double[] atmVolatilityArray,
		final FokkerPlanckGenerator fokkerPlanckGenerator)
		throws Exception
	{
		Map<JulianDate, Double> dateToVolatilityMap = new TreeMap<JulianDate, Double>();

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			FRAStandardCapFloor fraCap = MakeCap (
				new JulianDate (valuationParams.valueDate()),
				forwardLabel,
				maturityTenorArray[maturityTenorIndex],
				manifestMeasure,
				atmStrikeArray[maturityTenorIndex],
				fokkerPlanckGenerator
			);

			fraCap.stripPiecewiseForwardVolatility (
				valuationParams,
				null,
				curveSurfaceQuoteContainer,
				null,
				atmVolatilityArray[maturityTenorIndex],
				dateToVolatilityMap
			);

			System.out.println (
				"\t|| Cap  " + fraCap.maturityDate() + " | " + FormatUtil.FormatDouble (
					fraCap.stream().value (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null
					).get (
						"FairPremium"
					),
					1,
					2,
					100.
				) + "% |" + FormatUtil.FormatDouble (
					OTCFixFloat (
						new JulianDate (valuationParams.valueDate()),
						forwardLabel.currency(),
						maturityTenorArray[maturityTenorIndex],
						0.
					).value (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null
					).get (
						"FairPremium"
					),
					1,
					2,
					100.
				) + "% |" + FormatUtil.FormatDouble (fraCap.strike(), 1, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (atmVolatilityArray[maturityTenorIndex], 2, 2, 100.) + "% |" +
				FormatUtil.FormatDouble (
					fraCap.priceFromFlatVolatility (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null,
						atmVolatilityArray[maturityTenorIndex]
					),
					1,
					0,
					10000.
				) + " ||"
			);
		}

		return dateToVolatilityMap;
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

		String fraTenor = "3M";
		String currency = "GBP";
		String manifestMeasure = "ParForward";

		String[] maturityTenorArray =
		{
			 "1Y",
			 "2Y",
			 "3Y",
			 "4Y",
			 "5Y",
			 "7Y",
			"10Y"
		};
		double[] atmStrikeArray =
		{
			0.0788, //  "1Y",
			0.0839, // 	"2Y",
			0.0864, //  "3Y",
			0.0869, //  "4Y",
			0.0879, //  "5Y",
			0.0890, //  "7Y",
			0.0889  // "10Y"
		};

		double[] atmVolatilityArray =
		{
			0.1550, //  "1Y",
			0.1775, // 	"2Y",
			0.1800, //  "3Y",
			0.1775, //  "4Y",
			0.1775, //  "5Y",
			0.1650, //  "7Y",
			0.1550  // "10Y"
		};

		JulianDate spotDate = DateUtil.CreateFromYMD (1995, DateUtil.FEBRUARY, 3);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, fraTenor);

		MergedDiscountForwardCurve discountCurve = MakeDC (spotDate, currency);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			discountCurve.nativeForwardCurve (fraTenor),
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\t||---------------------------------------------------");

		Map<JulianDate, Double> dateToLognormalCapVolatilityMap = ValueCap (
			forwardLabel,
			manifestMeasure,
			valuationParams,
			curveSurfaceQuoteContainer,
			maturityTenorArray,
			atmStrikeArray,
			atmVolatilityArray,
			new BlackScholesAlgorithm()
		);

		System.out.println ("\t||---------------------------------------------------");

		Map<JulianDate, Double> dateToNormalCapVolatilityMap = ValueCap (
			forwardLabel,
			manifestMeasure,
			valuationParams,
			curveSurfaceQuoteContainer,
			maturityTenorArray,
			atmStrikeArray,
			atmVolatilityArray,
			new BlackNormalAlgorithm()
		);

		System.out.println ("\n\n\t||---------------------------------------------------");

		System.out.println ("\t||-----  CALIBRATED FORWARD VOLATILITY NODES --------");

		System.out.println ("\t||---------------------------------------------------\n");

		for (Map.Entry<JulianDate, Double> dateToCapVolatilityMapEntry :
			dateToLognormalCapVolatilityMap.entrySet())
		{
			System.out.println (
				"\t|| " + dateToCapVolatilityMapEntry.getKey() + " => " +
				FormatUtil.FormatDouble (dateToCapVolatilityMapEntry.getValue(), 2, 2, 100.) + "%  |" +
				FormatUtil.FormatDouble (
					dateToNormalCapVolatilityMap.get (dateToCapVolatilityMapEntry.getKey()),
					2,
					2,
					100.
				) + "%  ||"
			);
		}

		System.out.println ("\t||---------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
