
package org.drip.function.rdtor1solver;

import org.drip.function.definition.RdToR1;
import org.drip.function.rdtor1descent.LineStepEvolutionControl;

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
 * <i>BarrierFixedPointFinder</i> invokes the Iterative Finders for locating the Fixed Point of R<sup>d</sup>
 *  To R<sup>1</sup> Convex/Non-Convex Functions Under Inequality Constraints using Barrier Sequences of
 *  decaying Strengths. It exposes the following Functions:
 *
 *  <ul>
 * 		<li><i>InteriorFixedPointFinder</i> Constructor
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

public class BarrierFixedPointFinder
{
	private RdToR1 _objectiveFunction = null;
	private LineStepEvolutionControl _lineStepEvolutionControl = null;
	private RdToR1[] _inequalityConstraintMultivariateFunctionArray = null;
	private InteriorPointBarrierControl _interiorPointBarrierControl = null;

	/**
	 * <i>BarrierFixedPointFinder</i> Constructor
	 * 
	 * @param objectiveFunction The Objective Function
	 * @param inequalityConstraintMultivariateFunctionArray Array of Multivariate Inequality Constraint
	 * 		Functions
	 * @param interiorPointBarrierControl Interior Point Barrier Strength Control Parameters
	 * @param lineStepEvolutionControl Line Step Evolution Verifier Control Parameters
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public BarrierFixedPointFinder (
		final RdToR1 objectiveFunction,
		final RdToR1[] inequalityConstraintMultivariateFunctionArray,
		final InteriorPointBarrierControl interiorPointBarrierControl,
		final LineStepEvolutionControl lineStepEvolutionControl)
		throws Exception
	{
		if (null == (_objectiveFunction = objectiveFunction) ||
			null == (
				_inequalityConstraintMultivariateFunctionArray =
					inequalityConstraintMultivariateFunctionArray
			) ||
			null == (_interiorPointBarrierControl = interiorPointBarrierControl))
		{
			throw new Exception ("BarrierFixedPointFinder Constructor => Invalid Inputs");
		}

		_lineStepEvolutionControl = lineStepEvolutionControl;

		if (0 == _inequalityConstraintMultivariateFunctionArray.length) {
			throw new Exception ("BarrierFixedPointFinder Constructor => Invalid Inputs");
		}

		for (int constraintIndex = 0;
			constraintIndex < _inequalityConstraintMultivariateFunctionArray.length;
			++constraintIndex)
		{
			if (null == _inequalityConstraintMultivariateFunctionArray[constraintIndex]) {
				throw new Exception ("BarrierFixedPointFinder Constructor => Invalid Inputs");
			}
		}
	}

	/**
	 * Retrieve the Objective Function
	 * 
	 * @return The Objective Function
	 */

	public RdToR1 objectiveFunction()
	{
		return _objectiveFunction;
	}

	/**
	 * Retrieve the Array of Inequality Constraints
	 * 
	 * @return The Array of Inequality Constraints
	 */

	public RdToR1[] inequalityConstraintMultivariateFunctionArray()
	{
		return _inequalityConstraintMultivariateFunctionArray;
	}

	/**
	 * Retrieve the Line Step Evolution Interior Control Parameters
	 * 
	 * @return The Line Step Evolution Strength Control Parameters
	 */

	public LineStepEvolutionControl lineStepEvolutionControl()
	{
		return _lineStepEvolutionControl;
	}

	/**
	 * Retrieve the Interior Point Barrier Control Parameters
	 * 
	 * @return The Interior Point Barrier Control Parameters
	 */

	public InteriorPointBarrierControl interiorPointBarrierControl()
	{
		return _interiorPointBarrierControl;
	}

	/**
	 * Solve for the Optimal Variate-Inequality Constraint Multiplier Tuple using the Barrier Iteration
	 *  Parameters provided by the IPBC Instance
	 *  
	 * @param startingVariateArray The Starting Variate Sequence
	 * 
	 * @return The Optimal Variate-Inequality Constraint Multiplier Tuple
	 */

	public VariateInequalityConstraintMultiplier solve (
		final double[] startingVariateArray)
	{
		double barrierStrength = _interiorPointBarrierControl.initialStrength();

		int outstandingDecaySteps = _interiorPointBarrierControl.decayStepCount();

		double barrierDecayVelocity = _interiorPointBarrierControl.decayVelocity();

		VariateInequalityConstraintMultiplier variateInequalityConstraintMultiplier = null;
		double[] startingConstraintMultiplierArray =
			new double[_inequalityConstraintMultivariateFunctionArray.length];

		try {
			for (int constraintIndex = 0;
				constraintIndex < _inequalityConstraintMultivariateFunctionArray.length;
				++constraintIndex)
			{
				startingConstraintMultiplierArray[constraintIndex] = barrierStrength /
					_inequalityConstraintMultivariateFunctionArray[constraintIndex].evaluate (
						startingVariateArray
					);
			}

			variateInequalityConstraintMultiplier = new VariateInequalityConstraintMultiplier (
				false,
				startingVariateArray,
				startingConstraintMultiplierArray
			);
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

		while (0 <= --outstandingDecaySteps) {
			try {
				InteriorFixedPointFinder interiorFixedPointFinder = new InteriorFixedPointFinder (
					_objectiveFunction,
					_inequalityConstraintMultivariateFunctionArray,
					_lineStepEvolutionControl,
					_interiorPointBarrierControl,
					barrierStrength
				);

				if (null == (
						variateInequalityConstraintMultiplier = interiorFixedPointFinder.find (
							variateInequalityConstraintMultiplier
					))
				)
				{
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}

			barrierStrength *= barrierDecayVelocity;
		}

		return variateInequalityConstraintMultiplier;
	}
}
