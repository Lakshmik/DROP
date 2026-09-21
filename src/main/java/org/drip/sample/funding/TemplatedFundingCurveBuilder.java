
package org.drip.sample.funding;

import org.drip.analytics.date.*;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.valuation.*;
import org.drip.product.creator.SingleStreamComponentBuilder;
import org.drip.product.definition.*;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.MergedDiscountForwardCurve;
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
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
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
 * <i>TemplatedFundingCurveBuilder</i> sample demonstrates the usage of the different pre-built Funding Curve
 * 	Builders. It shows the following:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Construct the Array of Cash Instruments and their Quotes from the given set of parameters.
 *  	</li>
 *  	<li>
 * 			Construct the Array of Swap Instruments and their Quotes from the given set of parameters.
 *  	</li>
 *  	<li>
 * 			Construct the Cubic Tension KLK Hyperbolic Discount Factor Shape Preserver.
 *  	</li>
 *  	<li>
 * 			Construct the Cubic Tension KLK Hyperbolic Discount Factor Shape Preserver with Zero Rate
 * 				Smoothening applied.
 *  	</li>
 *  	<li>
 * 			Construct the Cubic Polynomial Discount Factor Shape Preserver.
 *  	</li>
 *  	<li>
 * 			Construct the Cubic Polynomial Discount Factor Shape Preserver with Zero Rate Smoothening
 * 				applied.
 *  	</li>
 *  	<li>
 * 			Construct the Discount Curve using the Bear Sterns' DENSE Methodology.
 *  	</li>
 *  	<li>
 * 			Construct the Discount Curve using the Bear Sterns' DUALDENSE Methodology.
 *  	</li>
 *  	<li>
 * 			Cross-Comparison of the Cash Calibration Instrument "Rate" metric across the different curve
 * 				construction methodologies.
 *  	</li>
 *  	<li>
 * 			Cross-Comparison of the Swap Calibration Instrument "Rate" metric across the different curve
 * 				construction methodologies.
 *  	</li>
 *  	<li>
 * 			Cross-Comparison of the generated Discount Factor across the different curve construction
 * 				Methodologies for different node points.
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/funding/README.md">Shape Preserving Local Funding Curve</a></td></tr>
 *  </table>
 *	<br>
 * 
 * @author Lakshmi Krishnamurthy
 */

public class TemplatedFundingCurveBuilder
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
		ForwardLabel forwardLabel = ForwardLabel.Create ("USD", "3M");

		SingleStreamComponent[] depositArray = new SingleStreamComponent[maturityDaysArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityDaysArray.length; ++maturityIndex) {
			depositArray[maturityIndex] = SingleStreamComponentBuilder.Deposit (
				effectiveDate,
				effectiveDate.addBusDays (maturityDaysArray[maturityIndex], currency),
				forwardLabel
			);
		}

		return depositArray;
	}

	private static final FixFloatComponent[] SwapInstrumentsFromMaturityTenor (
		final JulianDate effectiveDate,
		final String currency,
		final String[] maturityTenorArray)
		throws Exception
	{
		FixFloatComponent[] irsArray = new FixFloatComponent[maturityTenorArray.length];

		for (int maturityIndex = 0; maturityIndex < maturityTenorArray.length; ++maturityIndex) {
			irsArray[maturityIndex] = OTCIRS (
				effectiveDate,
				currency,
				maturityTenorArray[maturityIndex],
				0.
			);
		}

		return irsArray;
	}

	private static final double ComponentMetric (
		final Component component,
		final ValuationParams valluationParams,
		final MergedDiscountForwardCurve discountCurve,
		final String measure)
		throws Exception
	{
		return component.measureValue (
			valluationParams,
			null,
			MarketParamsBuilder.Create (discountCurve, null, null, null, null, null, null),
			null,
			measure
		);
	}

	/*
	 * This sample demonstrates the usage of the different pre-built Discount Curve Builders. It shows the
	 * 	following:
	 * 	- Construct the Array of Cash Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Array of Swap Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Cubic Tension KLK Hyperbolic Discount Factor Shape Preserver.
	 * 	- Construct the Cubic Tension KLK Hyperbolic Discount Factor Shape Preserver with Zero Rate
	 * 		Smoothening applied.
	 * 	- Construct the Cubic Polynomial Discount Factor Shape Preserver.
	 * 	- Construct the Cubic Polynomial Discount Factor Shape Preserver with Zero Rate Smoothening applied.
	 * 	- Construct the Discount Curve using the Bear Sterns' DENSE Methodology.
	 * 	- Construct the Discount Curve using the Bear Sterns' DUALDENSE Methodology.
	 * 	- Cross-Comparison of the Cash Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Cross-Comparison of the Swap Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 * 	- Cross-Comparison of the generated Discount Factor across the different curve construction
	 * 		Methodologies for different node points.
	 */

	private static final void TemplatedDiscountCurveBuilderSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		SingleStreamComponent[] depositArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[]
			{
				2,
				7,
				14,
				30,
				60,
				90,
				180,
				270,
				360,
				450,
				540,
				630,
				720
			}
		);

		double[] depositQuoteArray =
		{
			0.0017,
			0.0017,
			0.0018,
			0.0020,
			0.0023,
			0.0027,
			0.0032,
			0.0041,
			0.0054,
			0.0077,
			0.0104,
			0.0134,
			0.0160
		};
		String[] depositManifestMeasureArray =
		{
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate",
			"ForwardRate"
		};
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
		String[] swapManifestMeasureArray =
		{
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate",
			"SwapRate"
		};

		FixFloatComponent[] irsArray = SwapInstrumentsFromMaturityTenor (
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

		MergedDiscountForwardCurve klkHyperbolicShapePreserverDiscountCurve =
			ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
				"KLK_HYPERBOLIC_SHAPE_TEMPLATE",
				valuationParams,
				depositArray,
				depositQuoteArray,
				depositManifestMeasureArray,
				irsArray,
				swapQuoteArray,
				swapManifestMeasureArray,
				false
			);

		MergedDiscountForwardCurve klkHyperbolicSmootherDiscountCurve =
			ScenarioDiscountCurveBuilder.CubicKLKHyperbolicDFRateShapePreserver (
				"KLK_HYPERBOLIC_SMOOTH_TEMPLATE",
				valuationParams,
				depositArray,
				depositQuoteArray,
				depositManifestMeasureArray,
				irsArray,
				swapQuoteArray,
				swapManifestMeasureArray,
				true
			);

		MergedDiscountForwardCurve cubicPolyShapePreserverDiscountCurve =
			ScenarioDiscountCurveBuilder.CubicPolyDFRateShapePreserver (
				"CUBIC_POLY_SHAPE_TEMPLATE",
				valuationParams,
				depositArray,
				depositQuoteArray,
				depositManifestMeasureArray,
				irsArray,
				swapQuoteArray,
				swapManifestMeasureArray,
				false
			);

		MergedDiscountForwardCurve cubicPolySmootherDiscountCurve =
			ScenarioDiscountCurveBuilder.CubicPolyDFRateShapePreserver (
				"CUBIC_POLY_SMOOTH_TEMPLATE",
				valuationParams,
				depositArray,
				depositQuoteArray,
				depositManifestMeasureArray,
				irsArray,
				swapQuoteArray,
				swapManifestMeasureArray,
				true
			);

		MergedDiscountForwardCurve denseDiscountCurve = ScenarioDiscountCurveBuilder.DENSE (
			"DENSE",
			valuationParams,
			depositArray,
			depositQuoteArray,
			depositManifestMeasureArray,
			irsArray,
			swapQuoteArray,
			swapManifestMeasureArray,
			null
		);

		MergedDiscountForwardCurve dualDenseDiscountCurve = ScenarioDiscountCurveBuilder.DUALDENSE (
			"DENSE",
			valuationParams,
			depositArray,
			depositQuoteArray,
			"1M",
			depositManifestMeasureArray,
			irsArray,
			swapQuoteArray,
			"3M",
			swapManifestMeasureArray,
			null
		);

		System.out.println (
			"\n\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println ("\t||\t\t\t\t\t\tDEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t||   MATURITY  | KLK HYPER SHAPE | KLK HYPER SMOTH | CUBE POLY SHAPE | CUBE POLY SMOTH |      DENSE      |   DUAL  DENSE   |      INPUT"
		);

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] =>   " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						klkHyperbolicShapePreserverDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						klkHyperbolicSmootherDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						cubicPolyShapePreserverDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						cubicPolySmootherDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						denseDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						depositArray[depositIndex],
						valuationParams,
						dualDenseDiscountCurve,
						"Rate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println (
			"\n\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println ("\t||\t\t\t\t\t\tSWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t||   MATURITY  | KLK HYPER SHAPE | KLK HYPER SMOTH | CUBE POLY SHAPE | CUBE POLY SMOTH |      DENSE      |   DUAL  DENSE   |      INPUT"
		);

		System.out.println (
			"\t||---------------------------------------------------------------------------------------------------------------------------------------"
		);

		for (int irsIndex = 0; irsIndex < irsArray.length; ++irsIndex) {
			System.out.println (
				"\t|| [" + irsArray[irsIndex].maturityDate() + "] =    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						klkHyperbolicShapePreserverDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						klkHyperbolicSmootherDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						cubicPolyShapePreserverDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						cubicPolySmootherDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						denseDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					ComponentMetric (
						irsArray[irsIndex],
						valuationParams,
						dualDenseDiscountCurve,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					swapQuoteArray[irsIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println (
			"\n\t||-----------------------------------------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t||      DF     |   KLK HYPER SHAPE |  KLK HYPER SMOTH  |  CUBE POLY SHAPE  |  CUBE POLY SMOTH  |       DENSE       |     DUAL DENSE    "
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------------------------------------"
		);

		int endDateJulian = irsArray[irsArray.length - 1].maturityDate().julian();

		int startDateJulian = depositArray[0].maturityDate().julian();

		int dateIncrement = (endDateJulian - startDateJulian) / 20;

		for (int dateJulian = startDateJulian; dateJulian <= endDateJulian; dateJulian += dateIncrement) {
			System.out.println (
				"\t|| [" + new JulianDate (dateJulian) + "] =    " + FormatUtil.FormatDouble (
					klkHyperbolicShapePreserverDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					klkHyperbolicSmootherDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					cubicPolyShapePreserverDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					cubicPolySmootherDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					denseDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				) + "    |    " + FormatUtil.FormatDouble (
					dualDenseDiscountCurve.df (dateJulian),
					1,
					8,
					1.
				)
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

		String currency = "USD";

		JulianDate today = DateUtil.Today().addTenorAndAdjust ("0D", currency);

		TemplatedDiscountCurveBuilderSample (today, currency);

		EnvManager.TerminateEnv();
	}
}
