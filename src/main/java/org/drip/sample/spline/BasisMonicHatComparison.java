
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
 * <i>BasisMonicHatComparison</i> implements the comparison of the basis hat functions used in the
 * 	construction of the monic basis B Splines. It demonstrates the following:
 * 	- Construction of the Linear Cubic Rational Raw Hat Functions
 * 	- Construction of the Quadratic Cubic Rational Raw Hat Functions
 * 	- Construction of the Corresponding Processed Tension Basis Hat Functions
 * 	- Construction of the Wrapping Monic Functions
 * 	- Estimation and Comparison of the Ordered Derivatives
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

public class BasisMonicHatComparison
{

	/*
	 * This sample display the test of the different shape controller functions. It demonstrates the
	 * 	following:
	 * 	- Construct the Raw Cubic rational left Tension Basis using the specified shape controller and
	 * 		tension.
	 * 	- Construct the Raw Cubic rational right Tension Basis using the specified shape controller and
	 *  	tension.
	 *  - Construct the processed Cubic rational left Tension Basis using the Raw Cubic rational left Tension
	 *  	Basis.
	 *  - Construct the processed Cubic rational Right Tension Basis using the Raw Cubic rational Right
	 *  	Tension Basis.
	 *  - Construct the Segment Monic Basis Function using the left and the right processed hat functions.
	 *  - Display the response and the derivatives for the left/right cubic rational, and their corresponding
	 *  	processed tension hat basis functions.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void ShapeControllerTest (
		final String shapeController,
		final double tension)
		throws Exception
	{
		CubicRationalLeftRaw cubicRationalLeftRaw =
			new CubicRationalLeftRaw (1., 2., shapeController, tension);

		CubicRationalRightRaw cubicRationalRightRaw =
			new CubicRationalRightRaw (2., 3., shapeController, tension);

		TensionProcessedBasisHat leftTensionProcessedBasisHat =
			new TensionProcessedBasisHat (cubicRationalLeftRaw, 2);

		TensionProcessedBasisHat rightTensionProcessedBasisHat =
			new TensionProcessedBasisHat (cubicRationalRightRaw, 2);

		SegmentMonicBasisFunction segmentMonicBasisFunction =
			new SegmentMonicBasisFunction (leftTensionProcessedBasisHat, rightTensionProcessedBasisHat);

		double x = cubicRationalLeftRaw.left();

		while (x <= cubicRationalRightRaw.right()) {
			System.out.println (
				"\t|| Deriv[" + x + "] => " +
					FormatUtil.FormatDouble (segmentMonicBasisFunction.derivative (x, 1), 1, 5, 1.)
			);

			System.out.println (
				"\t|| Cubic Rational Left Deriv[" + x + "]  => " +
					FormatUtil.FormatDouble (cubicRationalLeftRaw.derivative (x, 3), 1, 5, 1.)
			);

			System.out.println (
				"\t|| Cubic Rational Right Deriv[" + x + "] => " +
					FormatUtil.FormatDouble (cubicRationalRightRaw.derivative (x, 3), 1, 5, 1.)
			);

			System.out.println (
				"\t|| TPBH Left Deriv[" + x + "]  => " +
					FormatUtil.FormatDouble (leftTensionProcessedBasisHat.derivative (x, 1), 1, 5, 1.)
			);

			System.out.println (
				"\t|| TPBH Right Deriv[" + x + "] => " +
				FormatUtil.FormatDouble (rightTensionProcessedBasisHat.derivative (x, 1), 1, 5, 1.)
			);

			x += 0.5;
		}
	}

	/*
	 * Sample illustrating the construction and usage of different monic basis hat shape controllers. This
	 * 	example illustrates the following:
	 * 	- Test Rational Linear Shape Control with 0.0 Tension Parameter (i.e., no shape control).
	 * 	- Test Rational Linear Shape Control with 1.0 Tension Parameter.
	 * 	- Test Rational Quadratic Shape Control with 1.0 Tension Parameter.
	 * 	- Test Exponential Shape Control with 1.0 Tension Parameter.
	 * 
	 *  	USE WITH CARE: This sample ignores errors and does not handle exceptions.
	 */

	private static final void BasisMonicHatComparisonSample()
		throws Exception
	{
		System.out.println ("\n\t||-------------------------------------------------------------------");

		System.out.println ("\t||----------------- NO SHAPE CONTROL --------------------------------");

		System.out.println ("\t||-------------------------------------------------------------------");

		ShapeControllerTest (BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR, 0.);

		System.out.println ("\n\t||-------------------------------------------------------------------");

		System.out.println ("\t||----------------- LINEAR SHAPE CONTROL; Tension 1.0 ---------------");

		System.out.println ("\t||-------------------------------------------------------------------");

		ShapeControllerTest (BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_LINEAR, 1.);

		System.out.println ("\n\t||-------------------------------------------------------------------");

		System.out.println ("\t||-------------- QUADRATIC SHAPE CONTROL; Tension 1.0 ---------------");

		System.out.println ("\t||-------------------------------------------------------------------");

		ShapeControllerTest (BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_QUADRATIC, 1.);

		System.out.println ("\n\t||-------------------------------------------------------------------");

		System.out.println ("\t||-------------- EXPONENTIAL SHAPE CONTROL; Tension 1.0 ---------------");

		System.out.println ("\t||-------------------------------------------------------------------");

		ShapeControllerTest (BasisHatShapeControl.SHAPE_CONTROL_RATIONAL_EXPONENTIAL, 1.);

		System.out.println ("\t||-------------------------------------------------------------------");
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

		BasisMonicHatComparisonSample();

		EnvManager.TerminateEnv();
	}
}
