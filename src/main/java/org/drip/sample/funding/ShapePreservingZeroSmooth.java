
package org.drip.sample.funding;

import org.drip.analytics.date.*;
import org.drip.analytics.definition.LatentStateStatic;
import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.market.otc.*;
import org.drip.param.creator.*;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.*;
import org.drip.product.creator.*;
import org.drip.product.definition.CalibratableComponent;
import org.drip.product.rates.*;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.pchip.LocalMonotoneCkGenerator;
import org.drip.spline.stretch.*;
import org.drip.state.creator.ScenarioDiscountCurveBuilder;
import org.drip.state.discount.*;
import org.drip.state.estimator.*;
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
 * <i>ShapePreservingZeroSmooth</i> demonstrates the usage of different shape preserving and smoothing
 * 	techniques involved in the funding curve creation. It shows the following:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Construct the Array of Cash/Swap Instruments and their Quotes from the given set of parameters.
 *  	</li>
 *  	<li>
 * 			Construct the Cash/Swap Instrument Set Stretch Builder.
 *  	</li>
 *  	<li>
 * 			Set up the Linear Curve Calibrator using the following parameters:
 *  		<ul>
 *  			<li>
 * 					Cubic Exponential Mixture Basis Spline Set
 *  			</li>
 *  			<li>
 * 					C<sub>k</sub> = 2
 * 					Segment Curvature Penalty = 2
 *  			</li>
 *  			<li>
 * 					Quadratic Rational Shape Controller
 *  			</li>
 *  			<li>
 * 					Natural Boundary Setting
 *  			</li>
 *  		</ul>
 *  	</li>
 *  	<li>
 * 			Set up the Global Curve Control parameters as follows:
 *  		<ul>
 *  			<li>
 * 					Zero Rate Quantification Metric
 *  			</li>
 *  			<li>
 * 					Cubic Polynomial Basis Spline Set
 *  			</li>
 *  			<li>
 * 					C<sub>k</sub> = 2
 * 					Segment Curvature Penalty = 2
 *  			</li>
 *  			<li>
 * 					Quadratic Rational Shape Controller
 *  			</li>
 *  			<li>
 * 					Natural Boundary Setting
 *  			</li>
 *  		</ul>
 *  	</li>
 *  	<li>
 * 			Set up the Local Curve Control parameters as follows:
 *  		<ul>
 *  			<li>
 * 					C1 Bessel Monotone Smoothener with no spurious extrema elimination and no monotone filter
 *  			</li>
 *  			<li>
 * 					Zero Rate Quantification Metric
 *  			</li>
 *  			<li>
 * 					Cubic Polynomial Basis Spline Set
 *  			</li>
 *  			<li>
 * 					C<sub>k</sub> = 2
 * 					Segment Curvature Penalty = 2
 *  			</li>
 *  			<li>
 * 					Quadratic Rational Shape Controller
 *  			</li>
 *  			<li>
 * 					Natural Boundary Setting
 *  			</li>
 *  		</ul>
 *  	</li>
 *  	<li>
 * 			Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the
 * 				array of Cash and Swap Stretches.
 *  	</li>
 *  	<li>
 * 			Construct the Globally Smoothened Discount Curve by applying the linear curve calibrator and the
 * 				Global Curve Control parameters to the array of Cash and Swap Stretches and the shape
 * 				preserving discount curve.
 *  	</li>
 *  	<li>
 * 			Construct the Locally Smoothened Discount Curve by applying the linear curve calibrator and the
 * 				Local Curve Control parameters to the array of Cash and Swap Stretches and the shape
 * 				preserving discount curve.
 *  	</li>
 *  	<li>
 * 			Cross-Comparison of the Cash/Swap Calibration Instrument "Rate" metric across the different curve
 * 				construction methodologies.
 *  	</li>
 *  	<li>
 *  		Cross-Comparison of the Swap Calibration Instrument "Rate" metric across the different curve
 *  			construction methodologies for a sequence of bespoke swap instruments.
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

public class ShapePreservingZeroSmooth
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

	/*
	 * This sample demonstrates the usage of different shape preserving and smoothing techniques involved in
	 * 	the discount curve creation. It shows the following:
	 * 	- Construct the Array of Cash/Swap Instruments and their Quotes from the given set of parameters.
	 * 	- Construct the Cash/Swap Instrument Set Stretch Builder.
	 * 	- Set up the Linear Curve Calibrator using the following parameters:
	 * 		- Cubic Exponential Mixture Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Set up the Global Curve Control parameters as follows:
	 * 		- Zero Rate Quantification Metric
	 * 		- Cubic Polynomial Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Set up the Local Curve Control parameters as follows:
	 * 		- C1 Bessel Monotone Smoothener with no spurious extrema elimination and no monotone filter
	 * 		- Zero Rate Quantification Metric
	 * 		- Cubic Polynomial Basis Spline Set
	 * 		- Ck = 2, Segment Curvature Penalty = 2
	 * 		- Quadratic Rational Shape Controller
	 * 		- Natural Boundary Setting
	 * 	- Construct the Shape Preserving Discount Curve by applying the linear curve calibrator to the array
	 * 		of Cash and Swap Stretches.
	 * 	- Construct the Globally Smoothened Discount Curve by applying the linear curve calibrator and the
	 * 		Global Curve Control parameters to the array of Cash and Swap Stretches and the shape preserving
	 * 		discount curve.
	 * 	- Construct the Locally Smoothened Discount Curve by applying the linear curve calibrator and the
	 * 		Local Curve Control parameters to the array of Cash and Swap Stretches and the shape preserving
	 *  	discount curve.
	 * 	- Cross-Comparison of the Cash/Swap Calibration Instrument "Rate" metric across the different curve
	 * 		construction methodologies.
	 *  - Cross-Comparison of the Swap Calibration Instrument "Rate" metric across the different curve
	 *  	construction methodologies for a sequence of bespoke swap instruments.
	 */

	private static final void ShapePreservingDFZeroSmoothSample (
		final JulianDate spotDate,
		final String currency)
		throws Exception
	{
		SingleStreamComponent[] depositArray = DepositInstrumentsFromMaturityDays (
			spotDate,
			currency,
			new int[]
			{
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
			0.0018,
			0.0020,
			0.0023
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

		LinearLatentStateCalibrator linearLatentStateCalibrator = new LinearLatentStateCalibrator (
			new SegmentCustomBuilderControl (
				MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_MIXTURE,
				new ExponentialMixtureSetParams (
					new double[]
					{
						0.01,
						0.05,
						0.25
					}
				),
				SegmentInelasticDesignControl.Create (
					2,
					2
				),
				new ResponseScalingShapeControl (
					true,
					new QuadraticRationalShapeControl (0.)
				),
				null
			),
			BoundarySettings.NaturalStandard(),
			MultiSegmentSequence.CALIBRATE,
			null,
			null
		);

		ValuationParams valuationParams = new ValuationParams (spotDate, spotDate, currency);

		MergedDiscountForwardCurve shapePreservingDiscountCurve =
			ScenarioDiscountCurveBuilder.ShapePreservingDFBuild (
				currency,
				linearLatentStateCalibrator,
				new LatentStateStretchSpec[]
				{
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"DEPOSIT",
						depositArray,
						"ForwardRate",
						depositQuoteArray
					),
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"EDF",
						SingleStreamComponentBuilder.ForwardRateFuturesPack (spotDate, 8, currency),
						"ForwardRate",
						new double[]
						{
							0.0027,
							0.0032,
							0.0041,
							0.0054,
							0.0077,
							0.0104,
							0.0134,
							0.0160
						}
					),
					LatentStateStretchBuilder.ForwardFundingStretchSpec (
						"SWAP",
						irsArray,
						"SwapRate",
						swapQuoteArray
					)
				},
				valuationParams,
				null,
				null,
				null,
				1.
			);

		CurveSurfaceQuoteContainer shapePreservingMarketParams =
			MarketParamsBuilder.Create (shapePreservingDiscountCurve, null, null, null, null, null, null);

		CurveSurfaceQuoteContainer globallySmoothMarketParams = MarketParamsBuilder.Create (
			ScenarioDiscountCurveBuilder.SmoothingGlobalControlBuild (
				shapePreservingDiscountCurve,
				linearLatentStateCalibrator,
				new GlobalControlCurveParams (
					LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
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
				valuationParams,
				null,
				null,
				null
			),
			null,
			null,
			null,
			null,
			null,
			null
		);

		CurveSurfaceQuoteContainer locallySmoothMarketParams = MarketParamsBuilder.Create (
			ScenarioDiscountCurveBuilder.SmoothingLocalControlBuild (
				shapePreservingDiscountCurve,
				linearLatentStateCalibrator,
				new LocalControlCurveParams (
					LocalMonotoneCkGenerator.C1_BESSEL,
					LatentStateStatic.DISCOUNT_QM_ZERO_RATE,
					new SegmentCustomBuilderControl (
						MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
						new PolynomialFunctionSetParams (4),
						SegmentInelasticDesignControl.Create (2, 2),
						new ResponseScalingShapeControl (true, new QuadraticRationalShapeControl (0.)),
						null
					),
					MultiSegmentSequence.CALIBRATE,
					null,
					null,
					false,
					false
				),
				valuationParams,
				null,
				null,
				null
			),
			null,
			null,
			null,
			null,
			null,
			null
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||               DEPOSIT INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||        SHAPE PRESERVING   | SMOOTHING #1  | SMOOTHING #2  |  INPUT QUOTE  ");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int depositIndex = 0; depositIndex < depositArray.length; ++depositIndex) {
			System.out.println (
				"\t|| [" + depositArray[depositIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
						shapePreservingMarketParams,
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
						globallySmoothMarketParams,
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					depositArray[depositIndex].measureValue (
						valuationParams,
						null,
						locallySmoothMarketParams,
						null,
						"Rate"
					),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					depositQuoteArray[depositIndex],
					1,
					6,
					1.
				)
			);
		}

		System.out.println ("\n\t----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||               SWAP INSTRUMENTS CALIBRATION RECOVERY");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||        SHAPE PRESERVING   | SMOOTHING #1  | SMOOTHING #2  |  INPUT QUOTE  ");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int irsIndex = 0; irsIndex < irsArray.length; ++irsIndex) {
			System.out.println (
				"\t|| [" + irsArray[irsIndex].maturityDate() + "] =>" + FormatUtil.FormatDouble (
					irsArray[irsIndex].measureValue (
						valuationParams,
						null,
						shapePreservingMarketParams,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					irsArray[irsIndex].measureValue (
						valuationParams,
						null,
						locallySmoothMarketParams,
						null,
						"CalibSwapRate"
					),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					swapQuoteArray[irsIndex],
					1,
					6,
					1.
				)
			);
		}

		CalibratableComponent[] calibratableComponentArray = SwapInstrumentsFromMaturityTenor (
			spotDate,
			currency,
			new String[]
			{
				"3Y",
				"6Y",
				"9Y",
				"12Y",
				"15Y",
				"18Y",
				"21Y",
				"24Y",
				"27Y",
				"30Y"
			}
		);

		System.out.println ("\n\t||----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||           BESPOKE SWAPS PAR RATE");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||        SHAPE PRESERVING   |  SMOOTHING #1 |  SMOOTHING #2");

		System.out.println ("\t||----------------------------------------------------------------");

		System.out.println ("\t||----------------------------------------------------------------");

		for (int calibratableComponentIndex = 0;
			calibratableComponentIndex < calibratableComponentArray.length;
			++calibratableComponentIndex)
		{
			System.out.println (
				"\t|| [" + calibratableComponentArray[calibratableComponentIndex].maturityDate() + "] =>" +
					FormatUtil.FormatDouble (
						calibratableComponentArray[calibratableComponentIndex].measureValue (
							valuationParams,
							null,
							shapePreservingMarketParams,
							null,
							"CalibSwapRate"
						),
					1,
					6,
					1.
				) + "   |   " + FormatUtil.FormatDouble (
					calibratableComponentArray[calibratableComponentIndex].measureValue (
						valuationParams,
						null,
						locallySmoothMarketParams,
						null,
						"CalibSwapRate"
					),
					1,
					6,
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

		JulianDate spotDate = DateUtil.CreateFromYMD (2017, DateUtil.DECEMBER, 21);

		String currency = "USD";

		ShapePreservingDFZeroSmoothSample (spotDate, currency);

		EnvManager.TerminateEnv();
	}
}
