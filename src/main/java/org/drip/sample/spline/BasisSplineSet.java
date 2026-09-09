
package org.drip.sample.spline;

import org.drip.function.r1tor1custom.QuadraticRationalShapeControl;
import org.drip.service.env.EnvManager;
import org.drip.spline.basis.*;
import org.drip.spline.params.*;
import org.drip.spline.segment.*;

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
 * <i>BasisSplineSet</i> implements Samples for the Construction and the usage of various basis spline
 * 	functions. It demonstrates the following:
 * 	- Construction of segment control parameters - polynomial (regular/Bernstein) segment control,
 * 		exponential/hyperbolic tension segment control, Kaklis-Pandelis tension segment control, and C1
 * 		Hermite.
 * 	- Control the segment using the rational shape controller, and the appropriate Ck.
 * 	- Estimate the node value and the node value Jacobian with the segment, as well as at the boundaries.
 * 	- Calculate the segment monotonicity.
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/SplineBuilderLibrary.md">Spline Builder Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/spline/README.md">Basis Monic Multic Tension Spline</a></td></tr>
 *  </table>
 *	<br>
 *  
 * @author Lakshmi Krishnamurthy
 */

public class BasisSplineSet
{

	private static final FunctionSet CreatePolynomialSpline()
		throws Exception
	{
		return FunctionSetBuilder.PolynomialBasisSet (new PolynomialFunctionSetParams (4));
	}

	private static final FunctionSet CreateBernsteinPolynomialSpline()
		throws Exception
	{
		return FunctionSetBuilder.BernsteinPolynomialBasisSet (new PolynomialFunctionSetParams (4));
	}

	private static final FunctionSet CreateExponentialTensionSpline()
		throws Exception
	{
		double tension = 1.;

		return FunctionSetBuilder.ExponentialTensionBasisSet (new ExponentialTensionSetParams (tension));
	}

	private static final FunctionSet CreateHyperbolicTensionSpline()
		throws Exception
	{
		double tension = 1.;

		return FunctionSetBuilder.HyperbolicTensionBasisSet (new ExponentialTensionSetParams (tension));
	}

	private static final FunctionSet CreateKaklisPandelisSpline()
		throws Exception
	{
		int polynomialTensionDegree = 2;

		return FunctionSetBuilder.KaklisPandelisBasisSet (new KaklisPandelisSetParams (polynomialTensionDegree));
	}

	/*
	 * This sample demonstrates the following:
	 * 
	 * 	- Construction of two segments, 1 and 2.
	 *  - Calibration of the segments to the left and the right node values
	 *  - Extraction of the segment Jacobians and segment monotonicity
	 *  - Estimate point value and the Jacobian
	 *  - Estimate the curvature penalty
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void TestSpline (
		final FunctionSet functionSet,
		final ResponseScalingShapeControl responseScalingShapeControl,
		final SegmentInelasticDesignControl segmentInelasticDesignControl)
		throws Exception
	{
		LatentStateResponseModel latentStateResponseModel1 = LatentStateResponseModel.Create (
			1.0,
			1.5,
			functionSet,
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		LatentStateResponseModel latentStateResponseModel2 = LatentStateResponseModel.Create (
			1.5,
			2.0,
			functionSet,
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t|| Y[" + 1.0 + "]: " + latentStateResponseModel1.responseValue (1.));

		System.out.println ("\t|| Y[" + 1.5 + "]: " + latentStateResponseModel1.responseValue (1.5));

		System.out.println (
			"\t|| Segment 1 Jacobian: " +
				latentStateResponseModel1.jackDCoeffDEdgeParams (25., 0., 20.25, null).displayString()
		);

		System.out.println (
			"\t|| Segment 1 Head: " + latentStateResponseModel1.jackDCoeffDEdgeInputs().displayString()
		);

		System.out.println ("\t|| Segment 1 Monotone Type: " + latentStateResponseModel1.monotoneType());

		System.out.println ("\t|| Segment 1 DPE: " + latentStateResponseModel1.curvatureDPE());

		System.out.println ("\t|| Y[" + 1.5 + "]: " + latentStateResponseModel2.responseValue (1.5));

		System.out.println ("\t|| Y[" + 2. + "]: " + latentStateResponseModel2.responseValue (2.));

		System.out.println (
			"\t|| Segment 2 Jacobian: " + latentStateResponseModel2.jackDCoeffDEdgeParams (
				latentStateResponseModel1,
				"Default",
				16.,
				null,
				Double.NaN,
				null
			).displayString()
		);

		System.out.println (
			"\t|| Segment 2 Regular Jacobian: " +
				latentStateResponseModel2.jackDCoeffDEdgeInputs().displayString()
		);

		System.out.println ("\t|| Segment 2 Monotone Type: " + latentStateResponseModel2.monotoneType());

		System.out.println ("\t|| Segment 2 DPE: " + latentStateResponseModel2.curvatureDPE());

		latentStateResponseModel2.calibrate (latentStateResponseModel1, 14., null);

		double x = 2.;

		System.out.println ("\t|| Value[" + x + "]: " + latentStateResponseModel2.responseValue (x));

		System.out.println (
			"\t|| Value Jacobian[" + x + "]: " +
				latentStateResponseModel2.jackDResponseDEdgeInput (x, 1).displayString()
		);

		System.out.println ("\t|| Segment 2 DPE: " + latentStateResponseModel2.curvatureDPE());
	}

	/*
	 * This sample demonstrates the following specifically for the C1 Hermite Splines, which are calibrated
	 *  using left and right node values, along with their derivatives:
	 * 
	 * 	- Construction of two segments, 1 and 2.
	 *  - Calibration of the segments to the left and the right node values
	 *  - Extraction of the segment Jacobians and segment monotonicity
	 *  - Estimate point value and the Jacobian
	 *  - Estimate the curvature penalty
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void TestC1HermiteSpline (
		final FunctionSet functionSet,
		final ResponseScalingShapeControl responseScalingShapeControl,
		final SegmentInelasticDesignControl segmentInelasticDesignControl)
		throws Exception
	{
		LatentStateResponseModel latentStateResponseModel1 = LatentStateResponseModel.Create (
			0.0,
			1.0,
			functionSet,
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		LatentStateResponseModel latentStateResponseModel2 = LatentStateResponseModel.Create (
			1.0,
			2.0,
			functionSet,
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t|| Y[" + 0.0 + "]: " + latentStateResponseModel1.responseValue (0.0));

		System.out.println ("\t|| Y[" + 1.0 + "]: " + latentStateResponseModel1.responseValue (1.0));

		System.out.println (
			"\t|| Segment 1 Jacobian: " + latentStateResponseModel1.jackDCoeffDEdgeParams (
				new double[]
				{
					0.,
					1.
				}, // Left/Right X
				new double[]
				{
					1.,
					4.
				}, // Left/Right Y
				new double[]
				{
					1.
				}, // Left Deriv
				new double[]
				{
					6.
				}, // Right Deriv
				null,
				null // Constraints, Fitness Weighted Response
			).displayString()
		);

		System.out.println (
			"\t|| Segment 1 Head: " + latentStateResponseModel1.jackDCoeffDEdgeInputs().displayString()
		);

		System.out.println ("\t|| Segment 1 Monotone Type: " + latentStateResponseModel1.monotoneType());

		System.out.println ("\t|| Segment 1 DPE: " + latentStateResponseModel1.curvatureDPE());

		System.out.println ("\t|| Y[" + 1.0 + "]: " + latentStateResponseModel2.responseValue (1.0));

		System.out.println ("\t|| Y[" + 2.0 + "]: " + latentStateResponseModel2.responseValue (2.0));

		System.out.println (
			"\t|| Segment 2 Jacobian: " + latentStateResponseModel2.jackDCoeffDEdgeParams (
				new double[]
				{
					1.,
					2.
				}, // Left/Right X
				new double[]
				{
					 4.,
					15.
				}, // Left/Right Y
				new double[]
				{
					6.
				}, // Left Deriv
				new double[]
				{
					17.
				}, // Right Deriv
				null,
				null // Constraints, Fitness Weighted Response
			).displayString());

		System.out.println (
			"\t|| Segment 2 Regular Jacobian: " +
				latentStateResponseModel2.jackDCoeffDEdgeInputs().displayString()
		);

		System.out.println ("\t|| Segment 2 Monotone Type: " + latentStateResponseModel2.monotoneType());

		System.out.println ("\t|| Segment 2 DPE: " + latentStateResponseModel2.curvatureDPE());

		latentStateResponseModel2.calibrate (latentStateResponseModel1, 14., null);

		double x = 2.;

		System.out.println ("\t|| Value[" + x + "]: " + latentStateResponseModel2.responseValue (x));

		System.out.println (
			"\t|| Value Jacobian[" + x + "]: " +
				latentStateResponseModel2.jackDResponseDEdgeInput (x, 1).displayString()
		);

		System.out.println ("\t|| Segment 2 DPE: " + latentStateResponseModel2.curvatureDPE());
	}

	/*
	 * This sample illustrates the construction and the usage of basis splines (all types, really). It shows
	 *  the following:
	 * 	- Construct a rational shape controller with the specified shape controller tension.
	 * 	- Construct the segment inelastic parameter that is C2 (iK = 2 sets it to C2), with second order
	 * 		curvature penalty, and without constraint.
	 * 	- Test the polynomial basis spline.
	 * 	- Test the Bernstein polynomial basis spline.
	 * 	- Test the exponential tension basis spline.
	 * 	- Test the hyperbolic tension basis spline.
	 * 	- Test the Kaklis-Pandelis basis spline.
	 * 	- Test the C1 Hermite basis spline.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void BasisSplineSetSample()
		throws Exception
	{
		int k = 2;
		double shapeControllerTension = 1.;
		int curvaturePenaltyDerivativeOrder = 2;

		ResponseScalingShapeControl responseScalingShapeControl = new ResponseScalingShapeControl (
			true,
			new QuadraticRationalShapeControl (shapeControllerTension)
		);

		SegmentInelasticDesignControl segmentInelasticDesignControl =
			SegmentInelasticDesignControl.Create (k, curvaturePenaltyDerivativeOrder);

		System.out.println ("\t||----------\n\t|| POLYNOMIAL \n\t||----------");

		TestSpline (CreatePolynomialSpline(), null, segmentInelasticDesignControl);

		System.out.println ("\t||--------------------\n\t|| BERNSTEINPOLYNOMIAL\n\t||--------------------");

		TestSpline (
			CreateBernsteinPolynomialSpline(),
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t||-----------\n\t|| EXPONENTIAL\n\t|| -----------");

		TestSpline (
			CreateExponentialTensionSpline(),
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t||-----------\n\t|| HYPERBOLIC\n\t|| -----------");

		TestSpline (
			CreateHyperbolicTensionSpline(),
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t||-----------\n\t|| KAKLIS-PANDELIS\n\t|| -----------");

		TestSpline (
			CreateKaklisPandelisSpline(),
			responseScalingShapeControl,
			segmentInelasticDesignControl
		);

		System.out.println ("\t||-----------\n\t|| C1 HERMITE\n\t|| -----------");

		TestC1HermiteSpline (
			CreatePolynomialSpline(),
			responseScalingShapeControl,
			SegmentInelasticDesignControl.Create (1, curvaturePenaltyDerivativeOrder)
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

		BasisSplineSetSample();

		EnvManager.TerminateEnv();
	}
}
