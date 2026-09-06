
package org.drip.sample.stretch;

import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.pchip.*;
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
 * <i>KnotInsertionPolynomialEstimator</i> demonstrates the Stretch builder and usage API. It shows the
 * 	following:
 * 	- Construction of segment control parameters - polynomial (regular/Bernstein) segment control,
 * 		exponential/hyperbolic tension segment control, Kaklis-Pandelis tension segment control.
 * 	- Perform the following sequence of tests for a given segment control for a predictor/response range
 * 		- Assign the array of Segment Builder Parameters - one per segment
 * 		- Construct the Stretch Instance
 * 		- Estimate, compute the segment-by-segment monotonicity and the Stretch Jacobian
 * 		- Construct a new Stretch instance by inserting a pair of of predictor/response knots
 * 		- Estimate, compute the segment-by-segment monotonicity and the Stretch Jacobian
 * 	- Demonstrate the construction, the calibration, and the usage of Local Control Segment Spline.
 * 	- Demonstrate the construction, the calibration, and the usage of Lagrange Polynomial Stretch.
 * 	- Demonstrate the construction, the calibration, and the usage of C1 Stretch with the desired customization.
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

public class KnotInsertionPolynomialEstimator
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

	private static final SegmentCustomBuilderControl BernsteinPolynomialSegmentControlParams (
		final int basisCount,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_BERNSTEIN_POLYNOMIAL,
			new PolynomialFunctionSetParams (basisCount),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl ExponentialTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_EXPONENTIAL_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl HyperbolicTensionSegmentControlParams (
		final double tension,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_HYPERBOLIC_TENSION,
			new ExponentialTensionSetParams (tension),
			segmentInelasticDesignControl,
			responseScalingShapeControl,
			null
		);
	}

	private static final SegmentCustomBuilderControl KaklisPandelisSegmentControlParams (
		final int tensionDegree,
		final SegmentInelasticDesignControl segmentInelasticDesignControl,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		return new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_KAKLIS_PANDELIS,
			new KaklisPandelisSetParams (tensionDegree),
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
				"SPLINE_STRETCH", 					// Name
				xArray, 							// predictors
				yArray, 							// responses
				segmentCustomBuilderControlArray, 	// Basis Segment Builder parameters
				null,  								// NULL segment Best Fit Response
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
				"\t\t|| Jacobian Y[" + x + "]: " +
					multiSegmentSequence.jackDResponseDCalibrationInput (x, 1).displayString()
			);

			x += 1.;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t|| SPLINE_STRETCH DPE: " + multiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence insertedMultiSegmentSequence = MultiSegmentSequenceModifier.InsertKnot (
			multiSegmentSequence, 				// The Original MSS
			9.,  								// Predictor Ordinate at which the Insertion is to be made
			10., 								// Response Value to be inserted
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

	private static final void TestHermiteCatmullRomCardinal()
		throws Exception
	{
		int k = 1;
		int basisCount = 4;
		double shapeControllerTension = 1.;
		int roughnessPenaltyDerivativeOrder = 2;
		double[] xArray = {
			0.,
			1.,
			2.,
			3.,
			4.
		};
		double[] yArray = {
			 1.,
			 4.,
			15.,
			40.,
			85.
		};
		double[] dYdXArray = {
			 1.,
			 6.,
			17.,
			34.,
			57.
		};

		SegmentCustomBuilderControl segmentCustomBuilderControl = new SegmentCustomBuilderControl (
			MultiSegmentSequenceBuilder.BASIS_SPLINE_POLYNOMIAL,
			new PolynomialFunctionSetParams (basisCount),
			SegmentInelasticDesignControl.Create (k, roughnessPenaltyDerivativeOrder),
			new ResponseScalingShapeControl (
				true,
				new QuadraticRationalShapeControl (shapeControllerTension)
			),
			null
		);

		double x = 0.;
		double xMaximum = 4.;
		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[segmentCount];
		SegmentPredictorResponseDerivative[] segmentPredictorResponseDerivativeLeftArray =
			new SegmentPredictorResponseDerivative[segmentCount];
		SegmentPredictorResponseDerivative[] segmentPredictorResponseDerivativeRightArray =
			new SegmentPredictorResponseDerivative[segmentCount];

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentCustomBuilderControlArray[segmentIndex] = segmentCustomBuilderControl;
		}

		MultiSegmentSequence multiSegmentSequence =
			MultiSegmentSequenceBuilder.CreateUncalibratedStretchEstimator (
				"SPLINE_STRETCH",
				xArray,
				segmentCustomBuilderControlArray
			);

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentPredictorResponseDerivativeLeftArray[segmentIndex] =
				new SegmentPredictorResponseDerivative (
					yArray[segmentIndex],
					new double[]
					{
						dYdXArray[segmentIndex]
					}
				);

			segmentPredictorResponseDerivativeRightArray[segmentIndex] =
				new SegmentPredictorResponseDerivative (
					yArray[segmentIndex + 1],
					new double[]
					{
						dYdXArray[segmentIndex + 1]
					}
				);
		}

		System.out.println (
			"Stretch Setup Succeeded: " +
			multiSegmentSequence.setupHermite (
				segmentPredictorResponseDerivativeLeftArray,
				segmentPredictorResponseDerivativeRightArray,
				null,
				null,
				MultiSegmentSequence.CALIBRATE
			)
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

			x += 0.5;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t|| SPLINE_STRETCH DPE: " + multiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence insertedMultiSegmentSequence = MultiSegmentSequenceModifier.InsertKnot (
			multiSegmentSequence,
			2.5,
			new SegmentPredictorResponseDerivative (
				27.5,
				new double[]
				{
					25.5
				}
			),
			new SegmentPredictorResponseDerivative (
				27.5,
				new double[]
				{
					25.5
				}
			)
		);

		x = 1.;

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Inserted Y[" + x + "] " + FormatUtil.FormatDouble (
					insertedMultiSegmentSequence.responseValue (x),
					2,
					2,
					1.
				) + " | " + insertedMultiSegmentSequence.monotoneType (x)
			);

			x += 0.5;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  SPLINE_STRETCH_INSERT DPE: " + insertedMultiSegmentSequence.curvatureDPE());

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence cardinalInsertedMultiSegmentSequence =
			MultiSegmentSequenceModifier.InsertCardinalKnot (multiSegmentSequence, 2.5, 0.);

		x = 1.;

		while (x <= xMaximum) {
			System.out.println (
				"\t||  Cardinal Inserted Y[" + x + "] " + FormatUtil.FormatDouble (
					cardinalInsertedMultiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + insertedMultiSegmentSequence.monotoneType (x)
			);

			x += 0.5;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t||  SPLINE_STRETCH_CARDINAL_INSERT DPE: " +
				cardinalInsertedMultiSegmentSequence.curvatureDPE()
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		MultiSegmentSequence catmullRomInsertedMultiSegmentSequence =
			MultiSegmentSequenceModifier.InsertCatmullRomKnot (multiSegmentSequence, 2.5);

		x = 1.;

		while (x <= xMaximum) {
			System.out.println (
				"\t||  Catmull-Rom Inserted Y[" + x + "] " + FormatUtil.FormatDouble (
					catmullRomInsertedMultiSegmentSequence.responseValue (x),
					1,
					2,
					1.
				) + " | " + insertedMultiSegmentSequence.monotoneType (x)
			);

			x += 0.5;
		}

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t||  SPLINE_STRETCH_CATMULL_ROM_INSERT DPE: " +
				catmullRomInsertedMultiSegmentSequence.curvatureDPE()
		);

		System.out.println ("\t||------------------------------------------------------------------------");
	}

	private static final void TestLagrangePolynomialStretch()
		throws Exception
	{
		SingleSegmentSequence singleSegmentLagrangePolynomial = new SingleSegmentLagrangePolynomial (
			new double[]
			{
				-2.,
				-1.,
				 2.,
				 5.
			 }
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println (
			"\t||  Setup: " + singleSegmentLagrangePolynomial.setup (
				0.25, 									// Left Edge Response Value
				new double[]
				{
					 0.25,
					 0.25,
					12.25,
					42.25
				},	// Array of Segment Response Values
				null, 									// Fitness Weighted Response
				BoundarySettings.NaturalStandard(), 	// Boundary Condition - Natural
				MultiSegmentSequence.CALIBRATE 			// Calibrate the Stretch predictors to the responses
			)
		);

		System.out.println (
			"\t||  Value = " + singleSegmentLagrangePolynomial.responseValue (2.16)
		);

		System.out.println (
			"\t||  Value Jacobian = " +
				singleSegmentLagrangePolynomial.jackDResponseDCalibrationInput (2.16, 1).displayString()
		);

		System.out.println (
			"\t||  Value Monotone Type: " + singleSegmentLagrangePolynomial.monotoneType (2.16)
		);

		System.out.println (
			"\t||  Is Locally Monotone: " + singleSegmentLagrangePolynomial.isLocallyMonotone()
		);

		System.out.println ("\t||------------------------------------------------------------------------");
	}

	private static final MultiSegmentSequence ConstructSpecifiedC1Stretch (
		final double[] xArray,
		final double[] yArray,
		final String generatorType,
		final SegmentCustomBuilderControl segmentCustomBuilderControl,
		final boolean eliminateSpuriousExtrema,
		final boolean applyMonotoneFilter)
	{
		LocalMonotoneCkGenerator localMonotoneCkGenerator = LocalMonotoneCkGenerator.Create (
			xArray,						// The Array of Predictor Ordinates
			yArray,						// The Array of Response Value
			generatorType,			  	// The C1 Generator Type
			eliminateSpuriousExtrema,	// TRUE => Eliminate Spurious Extremum
			applyMonotoneFilter			// TRUE => Apply Monotone Filter
		);

		int segmentCount = xArray.length - 1;
		SegmentCustomBuilderControl[] segmentCustomBuilderControlArray =
			new SegmentCustomBuilderControl[segmentCount]; 

		for (int segmentIndex = 0; segmentIndex < segmentCount; ++segmentIndex) {
			segmentCustomBuilderControlArray[segmentIndex] = segmentCustomBuilderControl;
		}

		return LocalControlStretchBuilder.CustomSlopeHermiteSpline (
			generatorType + "_LOCAL_STRETCH",
			xArray,
			yArray,
			localMonotoneCkGenerator.C1(),
			segmentCustomBuilderControlArray,
			null,
			MultiSegmentSequence.CALIBRATE
		);
	}

	private static final void C1GeneratedStretchTest (
		final MultiSegmentSequence multiSegmentSequence)
		throws Exception
	{
		double x = 1.;
		double xMaximum = 10.;

		System.out.println ("\t||------------------------------------------------------------------------");

		while (x <= xMaximum) {
			System.out.println (
				"\t|| Y[" + x + "] => " + FormatUtil.FormatDouble (
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

		MultiSegmentSequence insertedMultiSegmentSequence = MultiSegmentSequenceModifier.InsertKnot (
			multiSegmentSequence, 				// The Original MSS
			9.,  								// Predictor Ordinate at which the Insertion is to be made
			10., 								// Response Value to be inserted
			BoundarySettings.NaturalStandard(), // Boundary Condition - Natural
			MultiSegmentSequence.CALIBRATE 		// Calibrate the Stretch predictors to the responses
		);

		x = 1.;

		while (x <= xMaximum) {
			System.out.println (""
				+ "\t|| Inserted Y[" + x + "] " + FormatUtil.FormatDouble (
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
			"\t|| SPLINE_STRETCH_INSERT DPE: " + insertedMultiSegmentSequence.curvatureDPE()
		);

		System.out.println ("\t||------------------------------------------------------------------------");
	}

	private static final void StretchEstimationTestSequence()
		throws Exception
	{
		int k = 2;
		double tension = 1.;
		int polynomialBasisCount = 4;
		double shapeControllerTension = 1.;
		int kaklisPandelisTensionDegree = 2;
		int bernsteinBasisPolynomialCount = 4;
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

		ResponseScalingShapeControl responseScalingShapeControl = new ResponseScalingShapeControl (
			true,
			new QuadraticRationalShapeControl (shapeControllerTension)
		);

		SegmentInelasticDesignControl segmentInelasticDesignControl = SegmentInelasticDesignControl.Create (
			k,
			roughnessPenaltyDerivativeOrder
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  BERNSTEIN POLYNOMIAL");

		BasisSplineStretchTest (
			xArray,
			yArray,
			BernsteinPolynomialSegmentControlParams (
				bernsteinBasisPolynomialCount,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  POLYNOMIAL");

		BasisSplineStretchTest (
			xArray,
			yArray,
			PolynomialSegmentControlParams (
				polynomialBasisCount,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  EXPONENTIAL TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			ExponentialTensionSegmentControlParams (
				tension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  HYPERBOLIC TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			HyperbolicTensionSegmentControlParams (
				tension,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  KAKLIS-PANDELIS TENSION");

		BasisSplineStretchTest (
			xArray,
			yArray,
			KaklisPandelisSegmentControlParams (
				kaklisPandelisTensionDegree,
				segmentInelasticDesignControl,
				responseScalingShapeControl
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  HERMITE - CATMULL ROM - CARDINAL");

		TestHermiteCatmullRomCardinal();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  LAGRANGE POLYNOMIAL STRETCH");

		TestLagrangePolynomialStretch();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  AKIMA STRETCH");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_AKIMA,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 BESSEL/HERMITE");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_BESSEL,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 HARMONIC MONOTONE WITH FILTER");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_HARMONIC,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 HARMONIC MONOTONE WITHOUT FILTER");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_HARMONIC,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				false
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 HUYNH LE-FLOCH LIMITER STRETCH WITHOUT FILTER");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_HUYNH_LE_FLOCH,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 HYMAN 1983 MONOTONE");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_HYMAN83,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 HYMAN 1989 MONOTONE");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_HYMAN89,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 KRUGER STRETCH");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_KRUGER,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				true
			)
		);

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println();

		System.out.println ("\t||------------------------------------------------------------------------");

		System.out.println ("\t||  C1 VAN LEER LIMITER STRETCH WITHOUT FILTER");

		C1GeneratedStretchTest (
			ConstructSpecifiedC1Stretch (
				xArray,
				yArray,
				LocalMonotoneCkGenerator.C1_VAN_LEER,
				PolynomialSegmentControlParams (
					polynomialBasisCount,
					segmentInelasticDesignControl,
					responseScalingShapeControl
				),
				true,
				false
			)
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

		StretchEstimationTestSequence();

		EnvManager.TerminateEnv();
	}
}
