
package org.drip.sample.stretch;

import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.PolynomialFunctionSetParams;
import org.drip.spline.params.*;
import org.drip.spline.stretch.*;

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
 * <i>KnotInsertionSequenceAdjuster</i> demonstrates the Stretch Manipulation and Adjustment API. It shows
 * 	the following:
 * 	- Construct a simple Base Stretch.
 * 	- Clip a left Portion of the Stretch to construct a left-clipped Stretch.
 * 	- Clip a right Portion of the Stretch to construct a tight-clipped Stretch.
 *  - Compare the values across all the stretches to establish a) the continuity in the base smoothness is,
 *  	preserved, and b) Continuity across the predictor ordinate for the implied response value is also
 *  	preserved.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/SplineBuilderLibrary.md">Spline Builder Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/stretch/README.md">Knot Insertion Curvature Roughness Penalty</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class KnotInsertionSequenceAdjuster
{

	private static final SegmentCustomBuilderControl PolynomialSegmentControlParams (
		final int basisCount,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (basisCount),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final MultiSegmentSequence BasisSplineStretchTest (
		final double[] xArray,
		final double[] yArray,
		final SegmentCustomBuilderControl segmentCustomBuilderControl)
		throws Exception
	{
		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[segmentCount]; 

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentCustomBuilderControlArray[segmentIndex] = segmentCustomBuilderControl;
		}

		return MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
			"SPLINE_STRETCH", 					// Name
			xArray, 							// predictors
			yArray, 							// responses
			segmentCustomBuilderControlArray, 	// Basis Segment Builder parameters
			null,  								// NULL segment Best Fit Response
			BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
			MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
		);
	}

	private static final void StretchAdjusterTest()
		throws Exception
	{
		int k = 2;
		int polynomialBasisCount = 4;
		double shapeControllerTension = 1.;
		int roughnessPenaltyDerivativeOrder = 2;
		double[] xArray =
		{
			 1.0,
			 1.5,
			 2.0,
			 3.0,
			 4.0,
			 5.0,
			 6.5,
			 8.0,
			10.0
		};
		double[] yArray =
		{
			25.00,
			20.25,
			16.00,
			 9.00,
			 4.00,
			 1.00,
			 0.25,
			 4.00,
			16.00
		};

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  POLYNOMIAL");

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence baseMultiSegmentSequence = BasisSplineStretchTest (
			xArray,
			yArray,
			PolynomialSegmentControlParams (
				polynomialBasisCount,
				SegmentInelasticDesignControl.Create (k, roughnessPenaltyDerivativeOrder),
				new ResponseScalingShapeControl (
					false,
					new QuadraticRationalShapeControl (shapeControllerTension)
				)
			)
		);

		double x = baseMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		double xMaximum = baseMultiSegmentSequence.getRightPredictorOrdinateEdge();

		while (x <= xMaximum) {
			System.out.println (
				"\t||  Y[" + x + "] " + FormatUtil.FormatDouble (
					baseMultiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + baseMultiSegmentSequence.monotoneType (x)
			);

			System.out.println (
				"\t||  Jacobian Y[" + x + "]: " +
					baseMultiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  SPLINE_STRETCH_BASE DPE: " + baseMultiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  LEFT CLIPPED");

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence leftClippedMultiSegmentSequence =
			baseMultiSegmentSequence.clipLeft ("LEFT_CLIP", 1.66);

		x = baseMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		while (x <= xMaximum) {
			if (leftClippedMultiSegmentSequence.in (x)) {
				System.out.println (
					"\t|| Y[" + x + "] " + FormatUtil.FormatDouble (
						leftClippedMultiSegmentSequence.responseValue (x),
						1,
						2,
						1.
					) + " | " + leftClippedMultiSegmentSequence.monotoneType (x)
				);

				System.out.println (
					"\t|| Jacobian Y[" + x + "]: " +
						leftClippedMultiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
				);
			}

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t|| SPLINE_STRETCH_LEFT DPE: " + leftClippedMultiSegmentSequence.curvatureDPE()
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  RIGHT CLIPPED");

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (" \n---------- \n RIGHT CLIPPED \n ---------- \n");

		MultiSegmentSequence rightClippedMultiSegmentSequence = baseMultiSegmentSequence.clipRight (
			"RIGHT_CLIP",
			7.48
		);

		x = baseMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		while (x <= xMaximum) {
			if (rightClippedMultiSegmentSequence.in (x)) {
				System.out.println (
					"\t|| Y[" + x + "] " + FormatUtil.FormatDouble (
						rightClippedMultiSegmentSequence.responseValue (x),
						1,
						2,
						1.
					) + " | " + rightClippedMultiSegmentSequence.monotoneType (x)
				);

				System.out.println (
					"\t|| Jacobian Y[" + x + "]: " +
						rightClippedMultiSegmentSequence.jackDResponseDCalibrationInput (
							x,
							1
						).displayString()
				);
			}

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t|| SPLINE_STRETCH_RIGHT DPE: " + rightClippedMultiSegmentSequence.curvatureDPE()
		);

		x = baseMultiSegmentSequence.getLeftPredictorOrdinateEdge();

		xMaximum = baseMultiSegmentSequence.getRightPredictorOrdinateEdge();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t||                           BASE         ||      LEFT CLIPPED           ||      RIGHT CLIPPED"
		);

		System.out.println (
			"\t||-----------------------------------------------------------------------------------------------------"
		);

		while (x <= xMaximum) {
			String leftClippedValue = "\t||         ";
			String rightClippedValue = "\t||         ";
			String leftClippedMonotonocity = "\t||             ";
			String rightClippedMonotonocity = "\t||             ";

			if (leftClippedMultiSegmentSequence.in (x)) {
				leftClippedValue = FormatUtil.FormatDouble (
					leftClippedMultiSegmentSequence.responseValue (x),
					2,
					6,
					1.
				);

				leftClippedMonotonocity = leftClippedMultiSegmentSequence.monotoneType (x).toString();
			}

			if (rightClippedMultiSegmentSequence.in (x)) {
				rightClippedValue = FormatUtil.FormatDouble (
					rightClippedMultiSegmentSequence.responseValue (x),
					2,
					6,
					1.
				);

				rightClippedMonotonocity = rightClippedMultiSegmentSequence.monotoneType (x).toString();
			}

			System.out.println (
				"\t|| Y[" + FormatUtil.FormatDouble (
					x,
					2,
					3,
					1.
				) + "] => " + FormatUtil.FormatDouble (
					baseMultiSegmentSequence.responseValue (x),
					2,
					6,
					1.
				) + " | " + baseMultiSegmentSequence.monotoneType (x) + "  ||  " + leftClippedValue + " | " +
				leftClippedMonotonocity + "  ||  " + rightClippedValue + " | " + rightClippedMonotonocity
			);

			x += 0.5;
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

		StretchAdjusterTest();

		EnvManager.TerminateEnv();
	}
}
