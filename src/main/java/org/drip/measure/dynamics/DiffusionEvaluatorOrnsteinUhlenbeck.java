
package org.drip.measure.dynamics;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1operator.Flat;
import org.drip.measure.realization.JumpDiffusionVertex;
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
 * 
 *  This file is part of DROP, an open-source library targeting analytics/risk, transaction cost analytics,
 *  	asset liability management analytics, capital, exposure, and margin analytics, valuation adjustment
 *  	analytics, and portfolio construction analytics within and across fixed income, credit, commodity,
 *  	equity, FX, and structured products. It also includes auxiliary libraries for algorithm support,
 *  	numerical analysis, numerical optimization, spline builder, model validation, statistical learning,
 *  	graph builder/navigator, and computational support.
 *  
 *  	https://lakshmik.github.io/DROP/
 *  
 *  DROP is composed of three modules:
 *  
 *  - DROP Product Core - https://lakshmik.github.io/DROP-Product-Core/
 *  - DROP Portfolio Core - https://lakshmik.github.io/DROP-Portfolio-Core/
 *  - DROP Computational Core - https://lakshmik.github.io/DROP-Computational-Core/
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
 * 	- Main                     => https://lakshmik.github.io/DROP/
 * 	- Wiki                     => https://github.com/lakshmik/DROP/wiki
 * 	- GitHub                   => https://github.com/lakshmik/DROP
 * 	- Repo Layout Taxonomy     => https://github.com/lakshmik/DROP/blob/master/Taxonomy.md
 * 	- Javadoc                  => https://lakshmik.github.io/DROP/Javadoc/index.html
 * 	- Technical Specifications => https://github.com/lakshmik/DROP/tree/master/Docs/Internal
 * 	- Release Versions         => https://lakshmik.github.io/DROP/version.html
 * 	- Community Credits        => https://lakshmik.github.io/DROP/credits.html
 * 	- Issues Catalog           => https://github.com/lakshmik/DROP/issues
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
 * <i>DiffusionEvaluatorOrnsteinUhlenbeck</i> evaluates the Drift/Volatility of the Diffusion Random Variable
 * 	Evolution according to R<sup>1</sup> Ornstein Uhlenbeck Process. It provides the following Functionality:
 *
 *  <ul>
 * 		<li>Construct a Standard Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i></li>
 * 		<li>Construct a Zero-Mean Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i></li>
 * 		<li>Retrieve the Mean Reversion Level</li>
 * 		<li>Retrieve the Burstiness Parameter Function</li>
 * 		<li>Retrieve the Relaxation Time Function</li>
 * 		<li>Retrieve the Reference Relaxation Time Scale Function</li>
 * 		<li>Retrieve the Reference Burstiness Scale Function</li>
 * 		<li>Retrieve the Reference Mean Reversion Level Scale</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/measure/README.md">R<sup>d</sup> Continuous/Discrete Probability Measures</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/measure/dynamics/README.md">Jump Diffusion Evolution Evaluator Variants</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class DiffusionEvaluatorOrnsteinUhlenbeck
	extends DiffusionEvaluator
	implements OrnsteinUhlenbeck
{
	private double _meanReversionLevel = Double.NaN;
	private OrnsteinUhlenbeckDriftWander _ornsteinUhlenbeckDriftWander = null;

	/**
	 * Construct a Standard Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i>
	 * 
	 * @param meanReversionLevel The Mean Reversion Level
	 * @param burstinessFunction The Burstiness Function
	 * @param relaxationTimeFunction The Relaxation Time Function
	 * 
	 * @return The Standard Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i>
	 */

	public static final DiffusionEvaluatorOrnsteinUhlenbeck Standard (
		final double meanReversionLevel,
		final R1ToR1 burstinessFunction,
		final R1ToR1 relaxationTimeFunction)
	{
		try {
			return new DiffusionEvaluatorOrnsteinUhlenbeck (
				meanReversionLevel,
				burstinessFunction,
				relaxationTimeFunction,
				new LocalEvaluator()
				{
					@Override public double value (
						final JumpDiffusionVertex jumpDiffusionVertex)
						throws Exception
					{
						if (null == jumpDiffusionVertex) {
							throw new Exception (
								"DiffusionEvaluatorOrnsteinUhlenbeck::DriftLocalEvaluator::value => Invalid Inputs"
							);
						}

						return -1. * (jumpDiffusionVertex.value() - meanReversionLevel) /
							relaxationTimeFunction.evaluate (jumpDiffusionVertex.time());
					}
				},
				new LocalEvaluator()
				{
					@Override public double value (
						final JumpDiffusionVertex jumpDiffusionVertex)
						throws Exception
					{
						if (null == jumpDiffusionVertex) {
							throw new Exception (
								"DiffusionEvaluatorOrnsteinUhlenbeck::VolatilityLocalEvaluator::value => Invalid Inputs"
							);
						}

						double time = jumpDiffusionVertex.time();

						return burstinessFunction.evaluate (time) *
							Math.sqrt (1. / relaxationTimeFunction.evaluate (time));
					}
				}
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct a Zero-Mean Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i>
	 * 
	 * @param burstiness The Burstiness Parameter
	 * @param relaxationTime The Relaxation Time
	 * 
	 * @return The Zero-Mean Instance of <i>DiffusionEvaluatorOrnsteinUhlenbeck</i>
	 */

	public static final DiffusionEvaluatorOrnsteinUhlenbeck ZeroMean (
		final double burstiness,
		final double relaxationTime)
	{
		try {
			return Standard (0., new Flat (burstiness), new Flat (relaxationTime));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private DiffusionEvaluatorOrnsteinUhlenbeck (
		final double meanReversionLevel,
		final R1ToR1 burstinessFunction,
		final R1ToR1 relaxationTimeFunction,
		final LocalEvaluator localDriftEvaluator,
		final LocalEvaluator localVolatilityEvaluator)
		throws Exception
	{
		super (localDriftEvaluator, localVolatilityEvaluator);

		if (!NumberUtil.IsValid (_meanReversionLevel = meanReversionLevel)) {
			throw new Exception ("DiffusionEvaluatorOrnsteinUhlenbeck Constructor => Invalid Inputs");
		}

		_ornsteinUhlenbeckDriftWander = new OrnsteinUhlenbeckDriftWander (
			burstinessFunction,
			relaxationTimeFunction
		);
	}

	/**
	 * Retrieve the Mean Reversion Level
	 * 
	 * @return The Mean Reversion Level
	 */

	public double meanReversionLevel()
	{
		return _meanReversionLevel;
	}

	/**
	 * Retrieve the Burstiness Function
	 * 
	 * @return The Burstiness Function
	 */

	public R1ToR1 burstinessFunction()
	{
		return _ornsteinUhlenbeckDriftWander.burstinessFunction();
	}

	/**
	 * Retrieve the Relaxation Time Function
	 * 
	 * @return The Relaxation Time Function
	 */

	public R1ToR1 relaxationTimeFunction()
	{
		return _ornsteinUhlenbeckDriftWander.relaxationTimeFunction();
	}

	/**
	 * Retrieve the <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 * 
	 * @return The <i>OrnsteinUhlenbeckDriftWander</i> Instance
	 */

	public OrnsteinUhlenbeckDriftWander ornsteinUhlenbeckDriftWander()
	{
		return _ornsteinUhlenbeckDriftWander;
	}

	/**
	 * Retrieve the Reference Relaxation Time Scale Function
	 * 
	 * @return The Reference Relaxation Time Scale Function
	 */

	@Override public R1ToR1 referenceRelaxationTimeFunction()
	{
		return relaxationTimeFunction();
	}

	/**
	 * Retrieve the Reference Burstiness Scale Function
	 * 
	 * @return The Reference Burstiness Scale Function
	 */

	@Override public R1ToR1 referenceBurstinessFunction()
	{
		return burstinessFunction();
	}

	/**
	 * Retrieve the Reference Mean Reversion Level Scale
	 * 
	 * @return The Reference Mean Reversion Level Scale
	 */

	@Override public double referenceMeanReversionLevel()
	{
		return meanReversionLevel();
	}
}
