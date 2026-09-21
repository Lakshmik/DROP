
package org.drip.measure.dynamics;

import org.drip.function.definition.R1ToR1;
import org.drip.function.r1tor1operator.Flat;
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
 * <i>OrnsteinUhlenbeckDriftWander</i> contains the Drift/Wander Reference Parameter the guide the Random
 * 	Variable Evolution according to Ornstein-Uhlenbeck Mean Reverting Process. The References are:
 * 
 * <br><br>
 * 	<ul>
 * 		<li>
 * 			Almgren, R. F. (2009): Optimal Trading in a Dynamic Market
 * 				https://www.math.nyu.edu/financial_mathematics/content/02_financial/2009-2.pdf
 * 		</li>
 * 		<li>
 * 			Almgren, R. F. (2012): Optimal Trading with Stochastic Liquidity and Volatility <i>SIAM Journal
 * 				of Financial Mathematics</i> <b>3 (1)</b> 163-181
 * 		</li>
 * 		<li>
 * 			Geman, H., D. B. Madan, and M. Yor (2001): Time Changes for Levy Processes <i>Mathematical
 * 				Finance</i> <b>11 (1)</b> 79-96
 * 		</li>
 * 		<li>
 * 			Jones, C. M., G. Kaul, and M. L. Lipson (1994): Transactions, Volume, and Volatility <i>Review of
 * 				Financial Studies</i> <b>7 (4)</b> 631-651
 * 		</li>
 * 		<li>
 * 			Walia, N. (2006): <i>Optimal Trading - Dynamic Stock Liquidation Strategies</i> <b>Princeton
 * 				University</b>
 * 		</li>
 * 	</ul>
 *
 * 	It provides the following Functionality:
 *
 *  <ul>
 * 		<li>Construct a Standard Instance of <i>OrnsteinUhlenbeckDriftWander</i></li>
 * 		<li><i>OrnsteinUhlenbeckDriftWander</i> Constructor</li>
 * 		<li>Retrieve the Burstiness Function</li>
 * 		<li>Retrieve the Relaxation Time Function</li>
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

public class OrnsteinUhlenbeckDriftWander
{
	private R1ToR1 _burstinessFunction = null;
	private R1ToR1 _relaxationTimeFunction = null;

	/**
	 * Construct a Standard Instance of <i>OrnsteinUhlenbeckDriftWander</i>
	 * 
	 * @param burstiness Burstiness Parameter
	 * @param relaxationTime Relaxation Time
	 * 
	 * @return Standard Instance of <i>OrnsteinUhlenbeckDriftWander</i>
	 */

	public static final OrnsteinUhlenbeckDriftWander Standard (
		final double burstiness,
		final double relaxationTime)
	{
		try {
			return !NumberUtil.IsValid (burstiness) || 0. >= burstiness ||
				!NumberUtil.IsValid (relaxationTime) || 0. >= relaxationTime ? null :
				new OrnsteinUhlenbeckDriftWander (new Flat (burstiness), new Flat (relaxationTime));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * <i>OrnsteinUhlenbeckDriftWander</i> Constructor
	 * 
	 * @param burstinessFunction The Burstiness Function
	 * @param relaxationTimeFunction The Relaxation Time Function
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public OrnsteinUhlenbeckDriftWander (
		final R1ToR1 burstinessFunction,
		final R1ToR1 relaxationTimeFunction)
		throws Exception
	{
		if (null == (_burstinessFunction = burstinessFunction) ||
			null == (_relaxationTimeFunction = relaxationTimeFunction))
		{
			throw new Exception ("OrnsteinUhlenbeckDriftWander Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Burstiness Function
	 * 
	 * @return The Burstiness Function
	 */

	public R1ToR1 burstinessFunction()
	{
		return _burstinessFunction;
	}

	/**
	 * Retrieve the Relaxation Time Function
	 * 
	 * @return The Relaxation Time Function
	 */

	public R1ToR1 relaxationTimeFunction()
	{
		return _relaxationTimeFunction;
	}
}
