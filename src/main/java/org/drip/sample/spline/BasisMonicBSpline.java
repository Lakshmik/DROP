
package org.drip.sample.spline;

import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;
import org.drip.spline.bspline.*;

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
 * <i>BasisMonicBSpline</i> implements Samples for the Construction and the usage of various monic basis B
 * 	Splines. It demonstrates the following:
 * 	- Construction of segment B Spline Hat Basis Functions.
 * 	- Estimation of the derivatives and the basis envelope cumulative integrands.
 * 	- Estimation of the normalizer and the basis envelope cumulative normalized integrands.
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

public class BasisMonicBSpline
{

	private static final void TestMonicHatBasis (
		final String hatType,
		final String shapeController,
		final TensionBasisHat[] tensionBasisHatArray,
		final double[] predictorOrdinateArray,
		final String test)
		throws Exception
	{
		SegmentBasisFunction segmentBasisFunction = SegmentBasisFunctionGenerator.Monic (
			hatType,
			shapeController,
			predictorOrdinateArray,
			2,
			tensionBasisHatArray[0].tension()
		);

		double x = 1.;
		double xIncrement = 0.25;

		System.out.println ("\n\t||-------------------------------------------------");

		System.out.println ("\t||            " + test);

		System.out.println ("\t||-------------------------------------------------");

		System.out.println ("\t||-------------X---|---LEFT---|---RIGHT--|--MONIC--");

		System.out.println ("\t||-------------------------------------------------");

		while (x <= 3.) {
			System.out.println (
				"\t|| Response[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
					FormatUtil.FormatDouble (tensionBasisHatArray[0].evaluate (x), 1, 5, 1.) + " | " +
					FormatUtil.FormatDouble (tensionBasisHatArray[1].evaluate (x), 1, 5, 1.) + " | " +
					FormatUtil.FormatDouble (segmentBasisFunction.evaluate (x), 1, 5, 1.)
			);

			x += xIncrement;
		}

		System.out.println ("\t||------------------------------------------------");

		x = 1.;

		while (x <= 3.) {
			System.out.println (
				"\t|| NormCumulative[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
					FormatUtil.FormatDouble (segmentBasisFunction.normalizedCumulative (x), 1, 5, 1.)
			);

			x += xIncrement;
		}

		System.out.println ("\t||------------------------------------------------");

		x = 1.;
		int order = 1;

		while (x <= 3.0) {
			System.out.println (
				"\t|| Deriv[" + FormatUtil.FormatDouble (x, 1, 3, 1.) + "] : " +
				FormatUtil.FormatDouble (segmentBasisFunction.derivative (x, order), 1, 5, 1.)
			);

			x += xIncrement;
		}

		System.out.println ("\t||-----------------------------------------------");

		System.out.println();
	}

	/*
	 * This sample illustrates the construction and usage of raw/processed basis tension splines, and their
	 * 	comparisons with the correspondingly constructed monic hat basis functions. It shows the following:
	 * 	- Construct the Processed Hyperbolic Tension Hat Pair from the co-ordinate arrays, the Ck, and the
	 * 		tension.
	 * 	- Implement and test the basis monic spline function using the constructed Processed Hyperbolic
	 * 		Tension Hat Pair and the Rational Linear Shape Controller.
	 * 	- Construct the Raw Hyperbolic Tension Hat Pair from the co-ordinate arrays and the tension.
	 * 	- Implement and test the basis monic spline function using the constructed Raw Hyperbolic Tension Hat
	 * 		Pair and the Rational Linear Shape Controller.
	 * 	- Construct the Processed Cubic Rational Tension Hat Pair from the co-ordinate arrays, Linear
	 * 		Rational Shape Controller, and no tension.
	 * 	- Implement and test the basis monic spline function using the constructed Flat Processed Cubic
	 * 		Tension Hat Pair and the Rational Linear Shape Controller.
	 * 	- Construct the Processed Cubic Rational Tension Hat Pair from the co-ordinate arrays, Linear
	 * 		Rational Shape Controller, and non-zero tension.
	 * 	- Implement and test the basis monic spline function using the constructed Processed Cubic Rational
	 * 		Tension Hat Pair and the Rational Linear Shape Controller.
	 * 	- Construct the Processed Cubic Rational Tension Hat Pair from the co-ordinate arrays, Quadratic
	 * 		Rational Shape Controller, and the tension.
	 * 	- Implement and test the basis monic spline function using the constructed Processed Cubic Rational
	 * 		Tension Hat Pair and the Quadratic Linear Shape Controller.
	 * 	- Construct the Processed Cubic Rational Tension Hat Pair from the co-ordinate arrays, Exponential
	 * 		Rational Shape Controller, and the tension.
	 * 	- Implement and test the basis monic spline function using the constructed Processed Cubic Rational
	 * 		Tension Hat Pair and the Rational Exponential Shape Controller.
	 */

	private static final void BasisMonicBSplineSample()
		throws Exception
	{
		double[] predictorOrdinateArray =
		{
			1.,
			2.,
			3.
		};

		TestMonicHatBasis (
			BasisHatPairGenerator.PROCESSED_TENSION_HYPERBOLIC,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			BasisHatPairGenerator.ProcessedHyperbolicTensionHatPair (
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				2,
				1.
			),
			predictorOrdinateArray,
			" PROCESSED HYPERBOLIC "
		);

		TestMonicHatBasis (
			BasisHatPairGenerator.RAW_TENSION_HYPERBOLIC,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			BasisHatPairGenerator.HyperbolicTensionHatPair (
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				1.
			),
			predictorOrdinateArray,
			" STRAIGHT  HYPERBOLIC "
		);

		TestMonicHatBasis (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			BasisHatPairGenerator.ProcessedCubicRationalHatPair (
				BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				2,
				0.
			),
			predictorOrdinateArray,
			"     CUBIC     FLAT   "
		);

		TestMonicHatBasis (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
			BasisHatPairGenerator.ProcessedCubicRationalHatPair (
				BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR,
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				2,
				1.
			),
			predictorOrdinateArray,
			" CUBIC LINEAR RATIONAL "
		);

		TestMonicHatBasis (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_QUADRATIC,
			BasisHatPairGenerator.ProcessedCubicRationalHatPair (
				BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_QUADRATIC,
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				2,
				1.
			),
			predictorOrdinateArray,
			" CUBIC  QUAD  RATIONAL "
		);

		TestMonicHatBasis (
			BasisHatPairGenerator.PROCESSED_CUBIC_RATIONAL,
			BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_EXPONENTIAL,
			BasisHatPairGenerator.ProcessedCubicRationalHatPair (
				BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_EXPONENTIAL,
				predictorOrdinateArray[0],
				predictorOrdinateArray[1],
				predictorOrdinateArray[2],
				2,
				1.
			),
			predictorOrdinateArray,
			" CUBIC  EXP  RATIONAL "
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

		BasisMonicBSplineSample();

		EnvManager.TerminateEnv();
	}
}
