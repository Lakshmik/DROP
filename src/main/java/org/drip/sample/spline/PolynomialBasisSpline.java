
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
 * <i>PolynomialBasisSpline</i> implements Samples for the Construction and the usage of polynomial (both
 * 	regular and Hermite) basis spline functions. It demonstrates the following:
 * 	- Control the polynomial segment using the rational shape controller, the appropriate Ck, and the basis
 * 		function.
 * 	- Demonstrate the variational shape optimization behavior.
 * 	- Estimate the node value and the node value Jacobian with the segment, as well as at the boundaries.
 * 	- Calculate the segment monotonicity and the curvature penalty.
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

public class PolynomialBasisSpline
{

	/*
	 * This sample demonstrates the following:
	 * 
	 * 	- Construction of two segments, 1 and 2.
	 *  - Calibration of the segments to the left and the right node values
	 *  - Extraction of the segment Jacobians and segment monotonicity
	 *  - Estimate point value and the Jacobian, monotonicity, and curvature penalty
	 */

	private static final void TestPolynomialSpline (
		final int basisCount,
		final int ck,
		final int roughnessPenaltyDerivativeOrder,
		final ResponseScalingShapeControl responseScalingShapeControl)
		throws Exception
	{
		System.out.println (
			"\t||------------------------------ \n\t||     POLYNOMIAL n = " + basisCount + "; Ck = " + ck +
				"\n\t|| ------------------------------ \n"
		);

		SegmentInelasticDesignControl segmentInelasticDesignControl =
			SegmentInelasticDesignControl.Create (ck, roughnessPenaltyDerivativeOrder);

		FunctionSet functionSet =
			FunctionSetBuilder.PolynomialBasisSet (new PolynomialFunctionSetParams (basisCount));

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
	 * This sample demonstrates the following specifically for the Ck Hermite Splines, which are calibrated
	 *  using left and right node values, along with their derivatives:
	 * 
	 * 	- Construction of two segments, 1 and 2.
	 *  - Calibration of the segments to the left and the right node values
	 *  - Extraction of the segment Jacobians and segment monotonicity
	 *  - Estimate point value and the Jacobian, monotonicity, and curvature penalty
	 */

	private static final void TestC1HermiteSpline (
		final int basisCount,
		final int ck,
		final int roughnessPenaltyDerivativeOrder,
		final ResponseScalingShapeControl rssc)
		throws Exception
	{
		System.out.println (
			"\t||------------------------------ \n\t||     HERMITE POLYNOMIAL n = " + basisCount + "; Ck = "
				+ ck + "\n\t|| ------------------------------ \n"
			);

		SegmentInelasticDesignControl segmentInelasticDesignControl =
			SegmentInelasticDesignControl.Create (ck, roughnessPenaltyDerivativeOrder);

		FunctionSet functionSet =
			FunctionSetBuilder.PolynomialBasisSet (new PolynomialFunctionSetParams (basisCount));

		LatentStateResponseModel latentStateResponseModel1 = LatentStateResponseModel.Create (
			0.,
			1.,
			functionSet,
			rssc,
			segmentInelasticDesignControl
		);

		LatentStateResponseModel latentStateResponseModel2 = LatentStateResponseModel.Create (
			1.,
			2.,
			functionSet,
			rssc,
			segmentInelasticDesignControl
		);

		latentStateResponseModel1.calibrateState (
			new SegmentStateCalibrationInputs (
				new double[]
				{
					0.,
					1.
				}, // Segment Calibration Nodes
				new double[]
				{
					1.,
					4.
				}, // Segment Calibration Values
				new double[]
				{
					1.
				}, // Segment Left Derivative
				new double[] {
					6.
				}, // Segment Left Derivative
				null,
				null // Segment Constraint AND Fitness Penalty Response
			)
		);

		System.out.println ("\t|| Y[" + 0.0 + "]: " + latentStateResponseModel1.responseValue (0.));

		System.out.println ("\t|| Y[" + 1.0 + "]: " + latentStateResponseModel1.responseValue (1.));

		System.out.println (
			"\t|| Segment 1 Head: " + latentStateResponseModel1.jackDCoeffDEdgeInputs().displayString()
		);

		System.out.println ("\t|| Segment 1 Monotone Type: " + latentStateResponseModel1.monotoneType());

		System.out.println ("\t|| Segment 1 DPE: " + latentStateResponseModel1.curvatureDPE());

		latentStateResponseModel2.calibrateState (
			new SegmentStateCalibrationInputs (
				new double[]
				{
					1.,
					2.
				}, // Segment Calibration Nodes
				new double[]
				{
					 4.,
					15.
				}, // Segment Calibration Values
				new double[]
				{
					6.
				}, // Segment Left Derivative
				new double[]
				{
					17.
				}, // Segment Left Derivative
				null, // Segment Constraint
				null // Fitness Penalty Response
			)
		);

		System.out.println ("\t|| Y[" + 1.0 + "]: " + latentStateResponseModel2.responseValue (1.0));

		System.out.println ("\t|| Y[" + 2.0 + "]: " + latentStateResponseModel2.responseValue (2.0));

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
	 * This sample illustrates the construction and usage for polynomial basis splines. It shows the
	 * 	following:
	 * 	- Construct a rational shape controller with the specified shape controller tension.
	 * 	- Set the Roughness Penalty to 2nd order Roughness Penalty Derivative Order.
	 * 	- Test the polynomial spline across different polynomial degrees and Ck's.
	 * 	- Test the C1 Hermite spline.
	 */

	private static final void PolynomialBasisSplineSample()
		throws Exception
	{
		double shapeControllerTension = 1.;
		int roughnessPenaltyDerivativeOrder = 2;

		ResponseScalingShapeControl responseScalingShapeControl = new ResponseScalingShapeControl (
			true,
			new QuadraticRationalShapeControl (shapeControllerTension)
		);

		TestPolynomialSpline (2, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (3, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (3, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (4, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (4, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (4, 2, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (5, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (5, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (5, 2, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (5, 3, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (6, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (6, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (6, 2, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (6, 3, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (6, 4, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 0, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 2, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 3, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 4, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		TestPolynomialSpline (7, 5, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);

		System.out.println ("\t|| -------------------- \n\t||  Ck HERMITE \n --------------------");

		TestC1HermiteSpline (4, 1, roughnessPenaltyDerivativeOrder, responseScalingShapeControl);
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

		PolynomialBasisSplineSample();

		EnvManager.TerminateEnv();
	}
}
