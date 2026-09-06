
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
 * <i>CurvatureRoughnessPenaltyFit</i> demonstrates the setting up and the usage of the curvature and
 * 	closeness of fit penalizing spline. It illustrates in detail the following steps:
 * 	- Set up the X Predictor Ordinate and the Y Response Value Set.
 * 	- Construct a set of Predictor Ordinates, their Responses, and corresponding Weights to serve as
 * 		weighted closeness of fit.
 * 	- Construct a rational shape controller with the desired shape controller tension parameters and Global
 * 		Scaling.
 * 	- Construct the segment inelastic parameter that is C2 (iK = 2 sets it to C2), with 2nd order roughness
 * 		penalty derivative, and without constraint.
 * 	- Construct the base, the base + 1 degree segment builder control.
 * 	- Construct the base, the elevated, and the best fit basis spline stretches.
 * 	- Compute the segment-by-segment monotonicity for all the three stretches.
 * 	- Compute the Stretch Jacobian for all the three stretches.
 * 	- Compute the Base Stretch Curvature Penalty Estimate.
 * 	- Compute the Elevated Stretch Curvature Penalty Estimate.
 * 	- Compute the Best Fit Stretch Curvature Penalty Estimate.
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

public class CurvatureRoughnessPenaltyFit
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
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final StretchBestFitResponse stretchBestFitResponse)
		throws Exception
	{
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[xArray.length - 1]; 

		for (int i = 0; i < xArray.length - 1; ++i) {
			segmentCustomBuilderControlArray[i] = segmentCustomBuilderControl;
		}

		return MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
			"SPLINE_STRETCH",
			xArray, // predictors
			yArray, // responses
			segmentCustomBuilderControlArray, // Basis Segment Builder parameters
			stretchBestFitResponse, // Stretch Fitness Weighted Response
			BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
			MultiSegmentSequence.CALIBRATE // Calibrate the Stretch predictors to the responses
		);
	}

	private static final void PenalizedCurvatureFitTest()
		throws Exception
	{
		int k = 2;
		int basisCount = 4;
		double shapeControllerTension = 1.;
		int roughnessPenaltyDerivativeOrder = 2;
		double[] xArray = {
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
		double[] yArray = {
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

		StretchBestFitResponse stretchBestFitResponse = StretchBestFitResponse.Create (
			new double[]
			{
				2.28,
				2.52,
				2.73,
				3.00,
				5.50,
				8.44,
				8.76,
				9.08,
				9.80,
				9.92
			},
			new double[]
			{
				14.27,
				12.36,
				10.61,
				 9.25,
				-0.50,
				 7.92,
				10.07,
				12.23,
				15.51,
				16.36
			},
			new double[]
			{
				1.09,
				0.82,
				1.34,
				1.10,
				0.50,
				0.79,
				0.65,
				0.49,
				0.24,
				0.21
			}
		);

		ResponseScalingShapeControl responseScalingShapeControl = new ResponseScalingShapeControl (
			false,
			new QuadraticRationalShapeControl (shapeControllerTension)
		);

		SegmentInelasticDesignControl segmentInelasticDesignControl = SegmentInelasticDesignControl.Create (
			k,
			roughnessPenaltyDerivativeOrder
		);

		System.out.println();

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t||         == ORIGINAL #1 ==      $$   == ORIGINAL #2 ==    $$   == BEST FIT ==    "
		);

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
		);

		SegmentCustomBuilderControl segmentCustomBuilderControl2 = PolynomialSegmentControlParams (
			basisCount + 1,
			segmentInelasticDesignControl,
			responseScalingShapeControl
		);

		MultiSegmentSequence multiSegmentSequence1 = BasisSplineStretchTest (
			xArray,
			yArray,
			PolynomialSegmentControlParams (
				basisCount,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			),
			null
		);

		MultiSegmentSequence multiSegmentSequence2 = BasisSplineStretchTest (
			xArray,
			yArray,
			segmentCustomBuilderControl2,
			null
		);

		MultiSegmentSequence bestFitMultiSegmentSequence = BasisSplineStretchTest (
			xArray,
			yArray,
			segmentCustomBuilderControl2,
			stretchBestFitResponse
		);

		double x = multiSegmentSequence1.getLeftPredictorOrdinateEdge();

		double xMaximum = multiSegmentSequence1.getRightPredictorOrdinateEdge();

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Y[" + FormatUtil.FormatDouble (x, 2, 2, 1., false) + "] " +
					FormatUtil.FormatDouble (multiSegmentSequence1.responseValue (x), 2, 2, 1., false) +
					" | " + multiSegmentSequence1.monotoneType (x) + " $$ " +
					FormatUtil.FormatDouble (multiSegmentSequence2.responseValue (x), 2, 2, 1., false) +
					" | " + multiSegmentSequence2.monotoneType (x) + " $$ " +
					FormatUtil.FormatDouble (bestFitMultiSegmentSequence.responseValue (x), 2, 2, 1., false)
					+ " | " + bestFitMultiSegmentSequence.monotoneType (x)
			);

			x += 0.25;
		}

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
		);

		System.out.println();

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
		);

		x = multiSegmentSequence1.getLeftPredictorOrdinateEdge();

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Jacobian Y[" + FormatUtil.FormatDouble (x, 2, 2, 1.) + "] => " +
					multiSegmentSequence1.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			System.out.println (
				"\t|| Jacobian Y[" + FormatUtil.FormatDouble (x, 2, 2, 1.) + "] => " +
					multiSegmentSequence2.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			System.out.println (
				"\t|| Jacobian Y[" + FormatUtil.FormatDouble (x, 2, 2, 1.) + "] => " +
					bestFitMultiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			System.out.println (
				"\t||--------------------------------------------------------------------------------------------------"
			);

			x += 0.25;
		}

		System.out.println();

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
		);

		System.out.println (
			"\t|| BASE #1  DPE: " + FormatUtil.FormatDouble (multiSegmentSequence1.curvatureDPE(), 10, 0, 1.)
		);

		System.out.println (
			"\t|| BASE #2  DPE: " + FormatUtil.FormatDouble (multiSegmentSequence2.curvatureDPE(), 10, 0, 1.)
		);

		System.out.println (
			"\t|| BEST FIT DPE: " +
				FormatUtil.FormatDouble (bestFitMultiSegmentSequence.curvatureDPE(), 10, 0, 1.)
		);

		System.out.println (
			"\t||--------------------------------------------------------------------------------------------------"
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

		PenalizedCurvatureFitTest();

		EnvManager.TerminateEnv();
	}
}
