
package org.drip.function.rdtor1descent;

import org.drip.numerical.common.NumberUtil;

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
 * <i>LineStepEvolutionControl</i> contains the Parameters required to compute the Valid a Line Step. The
 * 	References are:
 * 	<br>
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
 * 	It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Retrieve the Nocedal-Wright-Armijo Verifier Based Standard <i>LineStepEvolutionControl</i> Instance</li>
 * 		<li>Retrieve the Nocedal-Wright-Weak Curvature Verifier Based Standard <i>LineStepEvolutionControl</i> Instance</li>
 * 		<li>Retrieve the Nocedal-Wright-Strong Curvature Verifier Based Standard <i>LineStepEvolutionControl</i> Instance</li>
 * 		<li>Retrieve the Nocedal-Wright-Weak Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i> Instance</li>
 * 		<li>Retrieve the Nocedal-Wright-Strong Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i> Instance</li>
 * 		<li><i>LineStepEvolutionControl</i> Constructor</li>
 * 		<li>Retrieve the Line Evolution Verifier Instance</li>
 * 		<li>Retrieve the Reduction Factor per Step</li>
 * 		<li>Retrieve the Count of Reduction Steps</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/rdtor1descent/README.md">R<sup>d</sup> To R<sup>1</sup> Gradient Descent Techniques</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class LineStepEvolutionControl
{
	private int _reductionStepCount = -1;
	private double _reductionFactor = Double.NaN;
	private LineEvolutionVerifier _lineEvolutionVerifier = null;

	/**
	 * Retrieve the Nocedal-Wright-Armijo Verifier Based Standard <i>LineStepEvolutionControl</i> Instance
	 * 
	 * @param maximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Armijo Verifier Based Standard <i>LineStepEvolutionControl</i> Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightArmijo (
		final boolean maximizerCheck)
	{
		try {
			return new LineStepEvolutionControl (
				ArmijoEvolutionVerifier.NocedalWrightStandard (maximizerCheck),
				0.75,
				1
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Weak Curvature Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 * 
	 * @return The Nocedal-Wright-Weak Curvature Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightWeakCurvature()
	{
		try {
			return new LineStepEvolutionControl (
				CurvatureEvolutionVerifier.NocedalWrightStandard (false),
				0.75,
				1
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Strong Curvature Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 * 
	 * @return The Nocedal-Wright-Strong Curvature Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightStrongCurvature()
	{
		try {
			return new LineStepEvolutionControl (
				CurvatureEvolutionVerifier.NocedalWrightStandard (true),
				0.75,
				1
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Weak Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 * 
	 * @param maximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Weak Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i> Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightWeakWolfe (
		final boolean maximizerCheck)
	{
		try {
			return new LineStepEvolutionControl (
				WolfeEvolutionVerifier.NocedalWrightStandard (maximizerCheck, false),
				0.75,
				1
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the Nocedal-Wright-Strong Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 * 
	 * @param maximizerCheck TRUE - Perform a Check for the Function Maxima
	 * 
	 * @return The Nocedal-Wright-Strong Wolfe Verifier Based Standard <i>LineStepEvolutionControl</i>
	 * 	Instance
	 */

	public static final LineStepEvolutionControl NocedalWrightStrongWolfe (
		final boolean maximizerCheck)
	{
		try {
			return new LineStepEvolutionControl (
				WolfeEvolutionVerifier.NocedalWrightStandard (maximizerCheck, true),
				0.75,
				1
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>LineStepEvolutionControl</i> Constructor
	 * 
	 * @param lineEvolutionVerifier The Line Evolution Verifier Instance
	 * @param reductionFactor The Per-Step Reduction Factor
	 * @param reductionStepCount Count of Reduction Steps
	 * 
	 * @throws Exception Thrown if Inputs are Invalid
	 */

	public LineStepEvolutionControl (
		final LineEvolutionVerifier lineEvolutionVerifier,
		final double reductionFactor,
		final int reductionStepCount)
		throws Exception
	{
		if (null == (_lineEvolutionVerifier = lineEvolutionVerifier) ||
			!NumberUtil.IsValid (_reductionFactor = reductionFactor) || 1. <= _reductionFactor ||
			0 >= (_reductionStepCount = reductionStepCount))
		{
			throw new Exception ("LineStepEvolutionControl Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Line Evolution Verifier Instance
	 * 
	 * @return The Line Evolution Verifier Instance
	 */

	public LineEvolutionVerifier lineEvolutionVerifier()
	{
		return _lineEvolutionVerifier;
	}

	/**
	 * Retrieve the Reduction Factor per Step
	 * 
	 * @return The Reduction Factor per Step
	 */

	public double reductionFactor()
	{
		return _reductionFactor;
	}

	/**
	 * Retrieve the Count of Reduction Steps
	 * 
	 * @return The Count of Reduction Steps
	 */

	public int reductionStepCount()
	{
		return _reductionStepCount;
	}
}
