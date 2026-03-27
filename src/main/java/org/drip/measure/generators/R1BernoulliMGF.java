
package org.drip.measure.generators;

import org.drip.measure.pdf.R1BernoulliDistribution;
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
 * <i>R1BernoulliMGF</i> implements the MGF of R<sup>1</sup> Bernoulli Distribution. The References are:
 * 
 * <br>
 * 	<ul>
 * 		<li>
 * 			Bertsekas, D., and J. Tsitsiklis (2002): <i>Introduction to Probability</i>
 * 				<b>Athena Scientific</b> Belmont MA
 * 		</li>
 * 		<li>
 * 			Dekking, F., C. Kraaikamp, H. Lopuhaa, and L. Meester (2010): <i>A Modern Introduction to
 * 				Probability and Statistics 1st Edition</i> <b>Springer</b> London UK
 * 		</li>
 * 		<li>
 * 			McCullagh, P., and J. Nelder (1989): <i>Generalized Linear Models 2nd Edition</i> <b>Chapman and
 * 				Hall/CRC</b> Boca Raton FL
 * 		</li>
 * 		<li>
 * 			Orloff, J., and J. Bloom (2018): Conjugate Priors: Beta and Normal
 * 				https://math.mit.edu/~dav/05.dir/class15-prep.pdf
 * 		</li>
 * 		<li>
 * 			Wikipedia (2026): Bernoulli Distribution https://en.wikipedia.org/wiki/Bernoulli_distribution
 * 		</li>
 * 	</ul>
 * 
 *  It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>R1BernoulliMGF</i> Constructor</li>
 * 		<li>Evaluate the Moment Generating Function at <code>t</code></li>
 * 		<li>Retrieve the n<sup>th</sup> Non-central Moment</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/measure/README.md">R<sup>d</sup> Continuous/Discrete Probability Measures</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/measure/generators/README.md">R<sup>1</sup>/R<sup>d</sup> Moment/Probability Generating Functions</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1BernoulliMGF
	extends R1MomentGeneratingFunction
{

	/**
	 * <i>R1BernoulliMGF</i> Constructor
	 * 
	 * @param r1BernoulliDistribution Underlying R<sup>1</sup> Bernoulli Distribution
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1BernoulliMGF (
		final R1BernoulliDistribution r1BernoulliDistribution)
		throws Exception
	{
		super (r1BernoulliDistribution);
	}

	/**
	 * Evaluate the Moment Generating Function at <code>t</code>
	 * 
	 * @param t MGF <code>t</code>
	 * 
	 * @return Moment Generating Function evaluated at <code>t</code>
	 * 
	 * @throws Exception Thrown if the Moment Generating Function cannot be evaluated
	 */

	@Override public double evaluate (
		final double t)
		throws Exception
	{
		if (!NumberUtil.IsValid (t) || 0. > t) {
			throw new Exception ("R1BernoulliMGF::evaluate => t is Invalid");
		}

		double p = 1. - ((R1BernoulliDistribution) distribution()).p();

		return 1. - p + p * Math.exp (t);
	}

	/**
	 * Retrieve the n<sup>th</sup> Non-central Moment
	 * 
	 * @param momentIndex Non-central Moment Index
	 * 
	 * @return n<sup>th</sup> Non-central Moment
	 * 
	 * @throws Exception Thrown if the Non-central Moment cannot be calculated
	 */

	public double nonCentralMoment (
		final int momentIndex)
		throws Exception
	{
		if (0 >= momentIndex) {
			throw new Exception ("R1BernoulliMGF::nonCentralMoment - Invalid Moment Index");
		}

		return distribution().mean();
	}
}
