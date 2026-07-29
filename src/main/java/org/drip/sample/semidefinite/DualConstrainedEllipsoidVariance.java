
package org.drip.sample.semidefinite;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1.*;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;
import org.drip.function.rdtor1solver.*;
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
 * <i>DualConstrainedEllipsoidVariance</i> demonstrates the Application of the Interior Point Method for
 * 	Minimizing the Variance Across The Specified Ellipsoid under both Normalization and first Moment
 * 	Constraints. The References are:
 * 
 * <br><br>
 * 	<ul>
 * 		<li>
 * 			Armijo, L. (1966): Minimization of Functions having Lipschitz-Continuous First Partial
 * 				Derivatives <i>Pacific Journal of Mathematics</i> <b>16 (1)</b> 1-3
 * 		</li>
 * 		<li>
 * 			Nocedal, J., and S. Wright (1999): <i>Numerical Optimization</i> <b>Wiley</b>
 * 		</li>
 * 		<li>
 * 			Wolfe, P. (1969): Convergence Conditions for Ascent Methods <i>SIAM Review</i> <b>11 (2)</b>
 * 				226-235
 * 		</li>
 * 		<li>
 * 			Wolfe, P. (1971): Convergence Conditions for Ascent Methods; II: Some Corrections <i>SIAM
 * 				Review</i> <b>13 (2)</b> 185-188
 * 		</li>
 * 	</ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/semidefinite/README.md">Semi-Definite Constrained Ellipsoid Variance</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class DualConstrainedEllipsoidVariance
{

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

		String[] entityNameArray = new String[]
		{
			"IBM",
			"ATT",
			"ALU",
			"QCO",
			"INT",
			"MSF",
			"VER"
		};
		double[] entityReturnsArray = new double[]
		{
			0.0264,
			0.0332,
			0.0400,
			0.0468,
			0.0536,
			0.0604,
			0.0672
		};
		double entityDesignReturn = 0.0468;
		double[][] entityCovarianceMatrix = new double[][]
		{
			{1.00, 0.76, 0.80, 0.38, 0.60, 0.61, 0.51},
			{0.76, 1.00, 0.65, 0.35, 0.56, 0.43, 0.40},
			{0.80, 0.65, 1.00, 0.68, 0.74, 0.40, 0.51},
			{0.38, 0.35, 0.68, 1.00, 0.72, 0.02, 0.57},
			{0.60, 0.56, 0.74, 0.72, 1.00, 0.31, 0.67},
			{0.61, 0.43, 0.40, 0.02, 0.31, 1.00, 0.39},
			{0.51, 0.40, 0.51, 0.57, 0.67, 0.39, 1.00}
		};

		InteriorPointBarrierControl interiorPointBarrierControl = InteriorPointBarrierControl.Standard();

		System.out.println ("\n\n\t|------------------------------------------------------||");

		String header = "\t|     |";
		double equalityConstraintConstant = -1.;

		for (int entityIndex = 0; entityIndex < entityNameArray.length; ++entityIndex) {
			header += " " + entityNameArray[entityIndex] + "  |";
		}

		System.out.println (header + "|");

		System.out.println ("\t|------------------------------------------------------||");

		for (int entityIndexI = 0; entityIndexI < entityNameArray.length; ++entityIndexI) {
			String dump = "\t| " + entityNameArray[entityIndexI] + " ";

			for (int entityIndexJ = 0; entityIndexJ < entityNameArray.length; ++entityIndexJ) {
				dump += "|" + FormatUtil.FormatDouble (
					entityCovarianceMatrix[entityIndexI][entityIndexJ], 1, 2, 1.
				) + " ";
			}

			System.out.println (dump + "||");
		}

		System.out.println ("\t|------------------------------------------------------||\n\n");

		RdToR1[] equalityConstraintFunctionArray = new RdToR1[]
		{
			new AffineMultivariate (
				ObjectiveConstraintVariateSet.Unitary (entityCovarianceMatrix.length),
				equalityConstraintConstant
			),
			new AffineMultivariate (entityReturnsArray, -1. * entityDesignReturn)
		};

		double expectedReturn = 0.;
		int totalVariateCount = entityCovarianceMatrix.length + equalityConstraintFunctionArray.length;

		LagrangianMultivariate lagrangianMultivariate = new LagrangianMultivariate (
			new CovarianceEllipsoidMultivariate (entityCovarianceMatrix),
			equalityConstraintFunctionArray
		);

		double[] optimalVariateArray = new BarrierFixedPointFinder (
			lagrangianMultivariate,
			new RdToR1[]
			{
				new AffineBoundMultivariate (false, 0, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 0, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 1, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 1, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 2, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 2, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 3, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 3, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 4, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 4, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 5, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 5, totalVariateCount, 0.65),
				new AffineBoundMultivariate (false, 6, totalVariateCount, 0.05),
				new AffineBoundMultivariate (true, 6, totalVariateCount, 0.65)
			},
			interiorPointBarrierControl,
			LineStepEvolutionControl.NocedalWrightStrongWolfe (false)
		).solve (
			ObjectiveConstraintVariateSet.Uniform (
				entityCovarianceMatrix.length,
				lagrangianMultivariate.constraintCount()
			)
		).problemVariableArray();

		System.out.println ("\t|----------------------||");

		System.out.println ("\t|   OPTIMAL ENTITIES   ||");

		System.out.println ("\t|----------------------||");

		for (int entityIndex = 0; entityIndex < entityCovarianceMatrix.length; ++entityIndex) {
			System.out.println (
				"\t|   " + entityNameArray[entityIndex] + "   =>  " +
					FormatUtil.FormatDouble (optimalVariateArray[entityIndex], 2, 2, 100.) + "%  ||"
			);

			expectedReturn += optimalVariateArray[entityIndex] * entityReturnsArray[entityIndex];
		}

		System.out.println ("\t|----------------------||\n");

		System.out.println ("\t|------------------------------||");

		System.out.println (
			"\t| DESIGN RETURN    => " + FormatUtil.FormatDouble (entityDesignReturn, 1, 5, 1.) + " ||"
		);

		System.out.println (
			"\t| EXPECTED RETURN  => " + FormatUtil.FormatDouble (expectedReturn, 1, 5, 1.) + " ||"
		);

		System.out.println (
			"\t| OPTIMAL VARIANCE => " +
				FormatUtil.FormatDouble (lagrangianMultivariate.evaluate (optimalVariateArray), 1, 5, 1.) +
				" ||"
		);

		System.out.println ("\t|------------------------------||\n");

		EnvManager.TerminateEnv();
	}
}
