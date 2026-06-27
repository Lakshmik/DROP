
package org.drip.sample.capfloor;

import java.util.*;

import org.drip.analytics.date.*;
import org.drip.analytics.support.*;
import org.drip.function.r1tor1operator.Flat;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.period.*;
import org.drip.param.valuation.*;
import org.drip.pricer.option.BlackScholesAlgorithm;
import org.drip.product.creator.*;
import org.drip.product.definition.*;
import org.drip.product.fra.FRAStandardCapFloor;
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
 * <i>FRAStdCapFloorAnalysis</i> contains an analysis if the correlation and volatility impact on a Cap/Floor
 * 	of the standard FRA.
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

public class FRAStdCapFloorAnalysis
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

	/*
	 * Construct the Array of Deposit Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final CalibratableComponent[] DepositInstrumentsFromMaturityDays (
		final JulianDate effectiveDate,
		final int[] daysCount,
		final int futuresCount,
		final String currency)
		throws Exception
	{
		CalibratableComponent[] calibratableComponentArray =
			new CalibratableComponent[daysCount.length + futuresCount];

		for (int daysIndex = 0; daysIndex < daysCount.length; ++daysIndex) {
			calibratableComponentArray[daysIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (daysCount[daysIndex], currency),
				ForwardLabel.Create (currency, "3M")
			);
		}

		CalibratableComponent[] edfComponentArray = SingleStreamComponentBuilder.ForwardRateFuturesPack (
			effectiveDate,
			futuresCount,
			currency
		);

		for (int daysIndex = daysCount.length; daysIndex < daysCount.length + futuresCount; ++daysIndex) {
			calibratableComponentArray[daysIndex] = edfComponentArray[daysIndex - daysCount.length];
		}

		return calibratableComponentArray;
	}

	/*
	 * Construct the Array of Swap Instruments from the given set of parameters
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

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

	/*
	 * Construct the discount curve using the following steps:
	 * 	- Construct the array of cash instruments and their quotes.
	 * 	- Construct the array of swap instruments and their quotes.
	 * 	- Construct a shape preserving and smoothing KLK Hyperbolic Spline from the cash/swap instruments.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final MergedDiscountForwardCurve MakeDC (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		/*
		 * Construct the array of Deposit instruments and their quotes.
		 */

		CalibratableComponent[] depositComponentArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			new int[] {
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
		);

		double[] depositQuoteArray = new double[] {
			0.01200,
			0.01200,
			0.01200,
			0.01450,
			0.01550,
			0.01600,
			0.01660,
			0.01850
		};

		String[] depositManifestMeasureArray = new String[] {
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate"
		};

		/*
		 * Construct the array of Swap instruments and their quotes.
		 */

		double[] irsFixFloatQuoteArray = new double[] {
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

		String[] irsFixFloatManifestMeasureArray = new String[] {
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
		};

		CalibratableComponent[] irsFixFloatComponentArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
			new String[] {
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
			irsFixFloatQuoteArray
		);

		/*
		 * Construct a shape preserving and smoothing KLK Hyperbolic Spline from the cash/swap instruments.
		 */

		return ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
			"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
			new ValuationParams (spotDate, spotDate, "USD"),
			depositComponentArray,
			depositQuoteArray,
			depositManifestMeasureArray,
			irsFixFloatComponentArray,
			irsFixFloatQuoteArray,
			irsFixFloatManifestMeasureArray,
			false
		);
	}

	/*
	 * Construct an array of float-float swaps from the corresponding reference (6M) and the derived legs.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final FloatFloatComponent[] MakexM6MBasisSwap (
		final JulianDate spotDate,
		final String currency,
		final String[] maturityTenorArray,
		final int tenorInMonths)
		throws Exception
	{
		FloatFloatComponent[] floatFloatComponentArray = new FloatFloatComponent[maturityTenorArray.length];

		for (int maturityTenorIndex = 0;
			maturityTenorIndex < maturityTenorArray.length;
			++maturityTenorIndex)
		{
			floatFloatComponentArray[maturityTenorIndex] = OTCFloatFloat (
				spotDate,
				currency,
				tenorInMonths + "M",
				maturityTenorArray[maturityTenorIndex],
				0.
			);
		}

		return floatFloatComponentArray;
	}

	private static final ForwardCurve MakeFC (
		final JulianDate spotDate,
		final String currency,
		final MergedDiscountForwardCurve discountCurve,
		final int tenorInMonths,
		final String[] xM6MForwardTenorArray,
		final double[] xM6MBasisSwapQuoteArray)
		throws Exception
	{
		/*
		 * Construct the 6M-xM float-float basis swap.
		 */

		FloatFloatComponent[] floatFloatComponentArray = MakexM6MBasisSwap (
			spotDate,
			currency,
			xM6MForwardTenorArray,
			tenorInMonths
		);

		String basisTenor = tenorInMonths + "M";

		/*
		 * Calculate the starting forward rate off of the discount curve.
		 */

		double startingForward = discountCurve.forward (
			spotDate.julian(),
			spotDate.addTenor (basisTenor).julian()
		);

		/*
		 * Set the discount curve based component market parameters.
		 */

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			null,
			null,
			null,
			null,
			null,
			null
		);

		/*
		 * Construct the shape preserving forward curve off of Quartic Polynomial Basis Spline.
		 */

		return ScenarioForwardCurveBuilder.ShapePreservingForwardCurve (
			"QUARTIC_FWD" + basisTenor,
			ForwardLabel.Create (currency, basisTenor),
			new ValuationParams (spotDate, spotDate, currency),
			null,
			curveSurfaceQuoteContainer,
			null,
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (5),
			floatFloatComponentArray,
			"DerivedParBasisSpread",
			xM6MBasisSwapQuoteArray,
			startingForward
		);
	}

	private static final Map<String, ForwardCurve> MakeFC (
		final JulianDate date,
		final String currency,
		final MergedDiscountForwardCurve discountCurve)
		throws Exception
	{
		Map<String, ForwardCurve> forwardCurveMap = new HashMap<String, ForwardCurve>();

		/*
		 * Build and run the sampling for the 1M-6M Tenor Basis Swap from its instruments and quotes.
		 */

		ForwardCurve forwardCurve1M = MakeFC (
			date,
			currency,
			discountCurve,
			1,
			new String[] {
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
			new double[] {
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
		);

		forwardCurveMap.put ("1M", forwardCurve1M);

		/*
		 * Build and run the sampling for the 3M-6M Tenor Basis Swap from its instruments and quotes.
		 */

		ForwardCurve forwardCurve3M = MakeFC (
			date,
			currency,
			discountCurve,
			3,
			new String[] {
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
			new double[] {
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
		);

		forwardCurveMap.put ("3M", forwardCurve3M);

		/*
		 * Build and run the sampling for the 12M-6M Tenor Basis Swap from its instruments and quotes.
		 */

		ForwardCurve forwardCurve12M = MakeFC (
			date,
			currency,
			discountCurve,
			12,
			new String[] {
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
			new double[] {
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
		);

		forwardCurveMap.put ("12M", forwardCurve12M);

		return forwardCurveMap;
	}

	private static final void SetVolCorrelation (
		final int valueDate,
		final CurveSurfaceQuoteContainer curveSurfaceQuoteContainer,
		final ForwardLabel forwardLabel,
		final double forwardVolatility,
		final double fundingVolatility,
		final double forwardFundingCorrelation)
		throws Exception
	{
		FundingLabel fundingLabel = FundingLabel.Standard (forwardLabel.currency());

		curveSurfaceQuoteContainer.setForwardVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
				VolatilityLabel.Standard (forwardLabel),
				forwardLabel.currency(),
				forwardVolatility
			)
		);

		curveSurfaceQuoteContainer.setFundingVolatility (
			ScenarioDeterministicVolatilityBuilder.FlatForward (
				valueDate,
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
		/*
		 * Initialize the Credit Analytics Library
		 */

		EnvManager.InitEnv ("");

		double strike = 0.02;
		String currency = "USD";
		String fraTenor = "3M";
		String maturityTenor = "4Y";
		String manifestMeasure = "QuantoAdjustedParForward";

		JulianDate today = DateUtil.Today().addTenor ("0D");

		/*
		 * Construct the Discount Curve using its instruments and quotes
		 */

		MergedDiscountForwardCurve discountCurve = MakeDC (today, currency);

		Map<String, ForwardCurve> forwardCurveMap = MakeFC (today, currency, discountCurve);

		ForwardLabel forwardLabel = ForwardLabel.Create (currency, fraTenor);

		JulianDate effectiveDate = today.addTenor (fraTenor);

		Stream floatStream = new Stream (
			CompositePeriodBuilder.FloatingCompositeUnit (
				CompositePeriodBuilder.RegularEdgeDates (
					effectiveDate.julian(),
					fraTenor,
					maturityTenor,
					null
				),
				new CompositePeriodSetting (4, fraTenor, currency, null, 1.,null, null, null, null),
				new ComposableFloatingUnitSetting (
					fraTenor,
					CompositePeriodBuilder.EDGE_DATE_SEQUENCE_SINGLE,
					null,
					forwardLabel,
					CompositePeriodBuilder.REFERENCE_PERIOD_IN_ADVANCE,
					0.
				)
			)
		);

		FRAStandardCapFloor fraCap = new FRAStandardCapFloor (
			"FRA_CAP",
			floatStream,
			manifestMeasure,
			true,
			strike,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			null,
			new BlackScholesAlgorithm()
		);

		FRAStandardCapFloor fraFloor = new FRAStandardCapFloor (
			"FRA_FLOOR",
			floatStream,
			manifestMeasure,
			false,
			strike,
			new LastTradingDateSetting (
				LastTradingDateSetting.MID_CURVE_OPTION_QUARTERLY,
				"",
				Integer.MIN_VALUE
			),
			null,
			new BlackScholesAlgorithm()
		);

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = MarketParamsBuilder.Create (
			discountCurve,
			forwardCurveMap.get (fraTenor),
			null,
			null,
			null,
			null,
			null,
			null
		);

		ValuationParams valuationParams = new ValuationParams (today, today, currency);

		double[] forwardSigmaArray = new double[] {0.1, 0.2, 0.3, 0.4, 0.5};
		double[] sigmaForwardToDomXArray = new double[] {0.10, 0.15, 0.20, 0.25, 0.30};
		double[] correlationForwardForwardToDomXArray = new double[] {-0.99, -0.50, 0.00, 0.50, 0.99};

		System.out.println ("\tPrinting the Cap/Floor Output in Order (Left -> Right):");

		System.out.println ("\t\tCap Price");

		System.out.println ("\t\tCap Flat Price Vol (%)");

		System.out.println ("\t\tFloor Price");

		System.out.println ("\t\tFloor Flat Price Vol (%)");

		System.out.println ("\t-------------------------------------------------------------");

		System.out.println ("\t-------------------------------------------------------------");

		for (double forwardSigma : forwardSigmaArray) {
			for (double sigmaForwardToDomX : sigmaForwardToDomXArray) {
				for (double correlationForwardForwardToDomX : correlationForwardForwardToDomXArray) {
					SetVolCorrelation (
						today.julian(),
						curveSurfaceQuoteContainer,
						forwardLabel,
						forwardSigma,
						sigmaForwardToDomX,
						correlationForwardForwardToDomX
					);

					Map<String, Double> fraCapOutputMap = fraCap.value (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null
					);

					Map<String, Double> fraFloorOutputMap = fraFloor.value (
						valuationParams,
						null,
						curveSurfaceQuoteContainer,
						null
					);

					System.out.println (
						"\t[" + FormatUtil.FormatDouble (forwardSigma, 2, 0, 100.) + "%," +
						FormatUtil.FormatDouble (sigmaForwardToDomX, 2, 0, 100.) + "%," +
						FormatUtil.FormatDouble (correlationForwardForwardToDomX, 2, 0, 100.) + "%] =" +
						FormatUtil.FormatDouble (fraCapOutputMap.get ("Price"), 1, 4, 1.) + " | " +
						FormatUtil.FormatDouble (fraCapOutputMap.get ("FlatVolatility"), 1, 1, 100.) + "% | "
						+ FormatUtil.FormatDouble (fraFloorOutputMap.get ("Price"), 1, 4, 1.) + " | " +
						FormatUtil.FormatDouble (fraFloorOutputMap.get ("FlatVolatility"), 1, 1, 100.) +
							"% ||"
					);
				}
			}
		}

		System.out.println ("\t-------------------------------------------------------------");

		System.out.println ("\t-------------------------------------------------------------");

		EnvManager.TerminateEnv();
	}
}
