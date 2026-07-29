
package org.drip.function.rdtor1solver;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;
import org.drip.numerical.linearalgebra.R1MatrixUtil;

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
 * <i>NewtonFixedPointFinder</i> generates the Iterators for solving R<sup>d</sup> To R<sup>1</sup>
 * 	Convex/Non-Convex Functions Using the Multivariate Newton Method. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>NewtonFixedPointFinder</i> Constructor</li>
 * 		<li>Produce the Incremental Variate-Constraint Multiplier</li>
 * 		<li>Iterate Over to the Next Variate-Constraint Multiplier Tuple</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/rdtor1solver/README.md">R<sup>d</sup> To R<sup>1</sup> Solver</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class NewtonFixedPointFinder
	extends FixedRdFinder
{

	/**
	 * <i>NewtonFixedPointFinder</i> Constructor
	 * 
	 * @param objectiveFunction The Objective Function
	 * @param lineStepEvolutionControl The Line Step Evolution Control
	 * @param convergenceControl Convergence Control Parameters
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public NewtonFixedPointFinder (
		final RdToR1 objectiveFunction,
		final LineStepEvolutionControl lineStepEvolutionControl,
		final ConvergenceControl convergenceControl)
		throws Exception
	{
		super (objectiveFunction, lineStepEvolutionControl, convergenceControl);
	}

	/**
	 * Produce the Incremental Variate-Constraint Multiplier
	 * 
	 * @param currentVariateInequalityConstraintMultiplier The Current Variate-Constraint Multiplier Tuple
	 * 
	 * @return The Incremental Variate-Constraint Multiplier
	 */

	@Override public VariateInequalityConstraintMultiplier increment (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier)
	{
		if (null == currentVariateInequalityConstraintMultiplier) {
			return null;
		}

		RdToR1 objectiveFunction = objectiveFunction();

		double[] problemVariableArray = currentVariateInequalityConstraintMultiplier.problemVariableArray();

		double[] problemVariableIncrementArray = R1MatrixUtil.Product (
			R1MatrixUtil.InvertUsingGaussianElimination (objectiveFunction.hessian (problemVariableArray)),
			objectiveFunction.jacobian (problemVariableArray)
		);

		if (null == problemVariableIncrementArray) {
			return null;
		}

		for (int problemVariableDimensionIndex = 0;
			problemVariableDimensionIndex < problemVariableIncrementArray.length;
			++problemVariableDimensionIndex)
		{
			problemVariableIncrementArray[problemVariableDimensionIndex] =
				-1. * problemVariableIncrementArray[problemVariableDimensionIndex];
		}

		try {
			return new VariateInequalityConstraintMultiplier (true, problemVariableIncrementArray, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Iterate Over to the Next Variate-Constraint Multiplier Tuple
	 * 
	 * @param currentVariateInequalityConstraintMultiplier Current Variate-Constraint Multiplier Tuple
	 * @param incrementVariateInequalityConstraintMultiplier Incremental Variate-Constraint Multiplier Tuple
	 * @param incrementFraction The Incremental Fraction to be applied
	 * 
	 * @return The Next Variate-Constraint Multiplier Set
	 */

	@Override public VariateInequalityConstraintMultiplier next (
		final VariateInequalityConstraintMultiplier currentVariateInequalityConstraintMultiplier,
		final VariateInequalityConstraintMultiplier incrementVariateInequalityConstraintMultiplier,
		final double incrementFraction)
	{
		return VariateInequalityConstraintMultiplier.Add (
			currentVariateInequalityConstraintMultiplier,
			incrementVariateInequalityConstraintMultiplier,
			incrementFraction,
			null
		);
	}
}
