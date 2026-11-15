
package org.drip.sample.matrix;

import org.drip.numerical.common.*;
import org.drip.numerical.linearalgebra.*;
import org.drip.numerical.linearsolver.LinearSystem;
import org.drip.service.common.FormatUtil;
import org.drip.service.env.EnvManager;

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
 * <i>LinearAlgebra</i> implements Samples for Linear Algebra and Matrix Manipulations. It demonstrates the
 * 	following:
 *  
 * <br><br>
 *  <ul>
 *  	<li>
 * 			Compute the inverse of a matrix, and multiply with the original to recover the unit matrix
 *  	</li>
 *  	<li>
 * 			Solves system of linear equations using one the exposed techniques
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/matrix/README.md">Cholesky Factorization, PCA, and Eigenization</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class LinearAlgebra
{

	private static final void InverseVerifyDump (
		final String label,
		final double[][] a)
	{
		double[][] aInverse = R1MatrixUtil.InvertUsingGaussianElimination (a);

		System.out.println ("\t||--- TESTS FOR " + label + "---");

		System.out.println ("\t||---------------------------------");

		NumberUtil.Print2DArrayTriplet (
			"\t|| SOURCE" + label,
			"|| INVERSE" + label,
			"|| PRODUCT" + label,
			a,
			aInverse,
			R1MatrixUtil.Product (a, aInverse),
			false
		);

		System.out.println ("\t||---------------------------------\n\n");
	}

	private static final void MatrixManipulation()
	{
		InverseVerifyDump (
			"#A",
			new double[][]
			{
				{1, 2, 3},
				{4, 5, 6},
				{7, 8, 9.01}
			}
		);

		InverseVerifyDump (
			"#B",
			new double[][]
			{
				{ 0.1667,  0.0000,  0.0000,  0.0000},
				{ 0.0000,  0.0000,  0.0000,  0.1667},
				{-0.6667,  0.5000,  0.0000,  0.0000},
				{ 2.6667, -3.0000,  1.0000,  0.0000}
			}
		);

		InverseVerifyDump (
			"#C",
			new double[][]
			{
				{ 1.0000,  0.0000,  0.0000,  0.0000},
				{ 1.0000,  1.0000,  1.0000,  1.0000},
				{ 0.0000,  1.0000,  0.0000,  0.0000},
				{ 0.0000,  0.0000,  2.0000,  0.0000}
			}
		);

		InverseVerifyDump (
			"#D",
			new double[][]
			{
				{ 0.0000,  1.0000},
				{ 1.0000,  2.0000}
			}
		);

		InverseVerifyDump (
			"#E",
			new double[][]
			{
				{ 0.0000,  1.0000},
				{ 1.0000,  0.0000}
			}
		);

		InverseVerifyDump (
			"#F",
			new double[][]
			{
				{ 1.0000,  0.0000,  0.0000,  0.0000},
				{ 1.0000,  1.0000,  1.0000,  1.0000},
				{-1.0000,  1.0000,  0.0000,  0.0000},
				{ 1.0000,  2.0000,  3.0000,  4.0000}
			}
		);

		InverseVerifyDump (
			"#G",
			new double[][]
			{
				{ 0.0000,  1.0000,  0.0000,  0.0000},
				{ 0.0000,  0.0000,  2.0000,  0.0000},
				{ 0.0434,  0.0188, 16.0083, 24.0037},
				{ 0.0188,  0.0083, 24.0037, 48.0017}
			}
		);
	}

	private static final void LinearSystemSolver()
	{
		double[][] a =
		{
			{1.000, 0.500, 0.333,  0.000,  0.000, 0.000},
			{0.000, 0.000, 0.000,  1.000,  0.500, 0.333},
			{1.000, 1.000, 1.000, -1.000,  0.000, 0.000},
			{0.000, 0.500, 2.000,  0.000, -0.500, 0.000},
			{0.000, 1.000, 0.000,  0.000,  0.000, 0.000},
			{0.000, 0.000, 0.000,  0.000,  1.000, 0.000},
		};
		double[] b =
		{
			0.020,
			0.026,
			0.000,
			0.000,
			0.000,
			0.000
		};

		NumberUtil.Print2DArray ("\t|| COEFF", a, false);

		double[] transformedRHS = LinearSystem.SolveUsingGaussianElimination (a, b).getTransformedRHS();

		for (int i = 0; i < transformedRHS.length; ++i) {
			System.out.println (
				"\t|| GaussianElimination[" + i + "] = " +
					FormatUtil.FormatDouble (transformedRHS[i], 0, 6, 1.)
			);
		}

		for (int i = 0; i < 6; ++i) {
			double rhs = 0.;

			for (int j = 0; j < 6; ++j) {
				rhs += a[i][j] * transformedRHS[j];
			}

			System.out.println ("\t|| RHS[" + i + "]: " + rhs);
		}
	}

	/**
	 * Entry Point
	 * 
	 * @param argumentArray Command Line Argument Array
	 */

	public static final void main (
		final String[] argumentArray)
	{
		EnvManager.InitEnv ("");

		MatrixManipulation();

		LinearSystemSolver();

		EnvManager.TerminateEnv();
	}
}
