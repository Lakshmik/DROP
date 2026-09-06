	
package org.drip.sample.stretch;

import org.drip.function.r1tor1custom.LinearRationalShapeControl;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
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
 * <i>KnotInsertionTensionEstimator</i> demonstrates the Stretch builder and usage API. It shows the
 * 	following:
 * 	- Construction of segment control parameters - polynomial (regular/Bernstein) segment control,
 * 		exponential/hyperbolic tension segment control, Kaklis-Pandelis tension segment control.
 * 	- Tension Basis Spline Test using the specified predictor/response set and the array of segment custom
 * 		builder control parameters.
 * 	- Complete the full tension stretch estimation sample test.
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

public class KnotInsertionTensionEstimator
{

	private static final SegmentCustomBuilderControl KLKExponentialTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_EXPONENTIAL_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKHyperbolicTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalLinearTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_LINEAR_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl KLKRationalQuadraticTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KLK_RATIONAL_QUADRATIC_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final void BasisSplineStretchTest (
		final double[] xArray,
		final double[] yArray,
		final SegmentCustomBuilderControl segmentCustomBuilderControl)
		throws Exception
	{
		double x = 1.;
		double xMaximum = 10.;
		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[segmentCount]; 

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentCustomBuilderControlArray[segmentIndex] = segmentCustomBuilderControl;
		}

		MultiSegmentSequence multiSegmentSequence =
			MultiSegmentSequenceBuilder.CreateCalibratedStretchEstimator (
				"SPLINE_STRETCH",
				xArray, 							// predictors
				yArray, 							// responses
				segmentCustomBuilderControlArray, 	// Basis Segment Builder parameters
				null, 
				BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
			);

		System.out.println ("\t||------------------------------------------------------------------------");

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Y[" + x + "] " + FormatUtil.FormatDouble (
					multiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + multiSegmentSequence.monotoneType (x)
			);

			System.out.println (
				"\t|| Jacobian Y[" + x + "]: " +
					multiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t|| SPLINE_STRETCH DPE: " + multiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence insertedMultiSegmentSequence =
			MultiSegmentSequenceModifier.InsertKnot (
				multiSegmentSequence,
				9.,
				10.,
				BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
			);

		x = 1.;

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Inserted Y[" + x + "] " + FormatUtil.FormatDouble (
					insertedMultiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + insertedMultiSegmentSequence.monotoneType (x)
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t||  SPLINE_STRETCH_INSERT DPE: " + insertedMultiSegmentSequence.curvatureDPE()
		);

		System.out.println ("\t||------------------------------------------------------------------------");
	}

	private static final void TensionStretchEstimationSample()
		throws Exception
	{
		int k = 2;
		double klkTension = 1.;
		double shapeControllerTension = 1.;
		int curvaturePenaltyDerivativeOrder = 2;
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

		ResponseScalingShapeControl responseScalingShapeControl = new ResponseScalingShapeControl (
			false,
			new LinearRationalShapeControl (shapeControllerTension)
		);

		SegmentInelasticDesignControl segmentInelasticDesignControl = SegmentInelasticDesignControl.Create (
			k,
			curvaturePenaltyDerivativeOrder
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||    KLK HYPERBOLIC TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			KLKHyperbolicTensionSegmentControlParams (
				klkTension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||    KLK EXPONENTIAL TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			KLKExponentialTensionSegmentControlParams (
				klkTension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||    KLK RATIONAL LINEAR TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			KLKRationalLinearTensionSegmentControlParams (
				klkTension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||    KLK RATIONAL QUADRATIC TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			KLKRationalQuadraticTensionSegmentControlParams (
				klkTension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");
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

		TensionStretchEstimationSample();

		EnvManager.TerminateEnv();
	}
}
