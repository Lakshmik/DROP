
package org.drip.measure.pdf;

import org.drip.function.definition.R1ToR1;
import org.drip.measure.distribution.R1Continuous;
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
 * <i>R1BernoulliDistribution</i> implements the R<sup>1</sup> Bernoulli Probability Distribution Function.
 * 	The References are:
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
 * 		<li><i>R1BernoulliDistribution</i> Constructor</li>
 * 		<li>Retrieve the <i>p</i> Parameter</li>
 * 		<li>Retrieve the <i>q</i> Parameter</li>
 * 		<li>Lay out the Support of the PDF Range</li>
 * 		<li>Compute the Density under the Distribution at the given Variate</li>
 * 		<li>Compute the cumulative under the distribution to the given value</li>
 * 		<li>Compute the inverse cumulative under the distribution corresponding to the given value</li>
 * 		<li>Retrieve the Mean of the Distribution</li>
 * 		<li>Retrieve the Median of the Distribution</li>
 * 		<li>Retrieve the Mode of the Distribution</li>
 * 		<li>Retrieve the Variance of the Distribution</li>
 * 		<li>Retrieve the Skewness of the Distribution</li>
 * 		<li>Retrieve the Excess Kurtosis of the Distribution</li>
 * 		<li>Retrieve the Differential Entropy of the Distribution</li>
 * 		<li>Construct the Probability Generating Function</li>
 * 		<li>Retrieve the Fisher Information of the Distribution</li>
 * 		<li>Retrieve the n<sup>th</sup> Non-central Moment</li>
 * 		<li>Retrieve the n<sup>th</sup> Central Moment</li>
 * 		<li>Retrieve the Second Central Moment</li>
 * 		<li>Retrieve the Third Central Moment</li>
 * 		<li>Retrieve the Fourth Central Moment</li>
 * 		<li>Retrieve the Fifth Central Moment</li>
 * 		<li>Retrieve the Sixth Central Moment</li>
 * 		<li>Retrieve the Fourth Central Moment Using the First and the Second Moments</li>
 * 		<li>Retrieve the Fifth Central Moment Using the First and the Second Moments</li>
 * 		<li>Retrieve the Sixth Central Moment Using the First and the Second Moments</li>
 * 		<li>Retrieve the First Cumulant</li>
 * 		<li>Retrieve the Second Cumulant</li>
 * 		<li>Retrieve the Third Cumulant</li>
 * 		<li>Retrieve the Fourth Cumulant</li>
 * 		<li>Retrieve the Fifth Cumulant</li>
 * 		<li>Retrieve the Sixth Cumulant</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/measure/README.md">R<sup>d</sup> Continuous/Discrete Probability Measures</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/measure/pdf/README.md">Explicit R<sup>1</sup> and R<sup>d</sup> PDF's</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1BernoulliDistribution
	extends R1Continuous
{
	private double _p = Double.NaN;

	/**
	 * <i>R1BernoulliDistribution</i> Constructor
	 * 
	 * @param p "p"
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1BernoulliDistribution (
		final double p)
		throws Exception
	{
		if (Double.isNaN (_p = p) || 0. > _p || 1. < _p) {
			throw new Exception ("R1BernoulliDistribution Constructor => Invalid p");
		}
	}

	/**
	 * Retrieve the <i>p</i> Parameter
	 * 
	 * @return <i>p</i> Parameter
	 */

	public double p()
	{
		return _p;
	}

	/**
	 * Retrieve the <i>q</i> Parameter
	 * 
	 * @return <i>q</i> Parameter
	 */

	public double q()
	{
		return 1. - _p;
	}

	/**
	 * Lay out the Support of the PDF Range
	 * 
	 * @return Support of the PDF Range
	 */

	@Override public double[] support()
	{
		return new double[] {
			0.,
			1.
		};
	}

	/**
	 * Compute the Density under the Distribution at the given Variate
	 * 
	 * @param x Variate at which the Density needs to be computed
	 * 
	 * @return The Density
	 * 
	 * @throws Exception Thrown if the input is invalid
	 */

	public double density (
		final double x)
		throws Exception
	{
		if (Double.isNaN (x)) {
			throw new Exception ("R1BernoulliDistribution::density => Invalid x");
		}

		return 0. == x ? 1. - _p : 1. == x ? _p : 0.;
	}

	/**
	 * Compute the cumulative under the distribution to the given value
	 * 
	 * @param x Variate to which the cumulative is to be computed
	 * 
	 * @return The cumulative
	 * 
	 * @throws Exception Thrown if the inputs are invalid
	 */

	public double cumulative (
		final double x)
		throws Exception
	{
		if (Double.isNaN (x)) {
			throw new Exception ("R1BernoulliDistribution::cumulative => Invalid x");
		}

		return 0. > x ? 0. : 1. > x ? 1. - _p : 1.;
	}

	/**
	 * Compute the inverse cumulative under the distribution corresponding to the given value
	 * 
	 * @param cumulative Value corresponding to which the inverse cumulative is to be computed
	 * 
	 * @return The inverse cumulative
	 * 
	 * @throws Exception Thrown if the Input is invalid
	 */

	public double invCumulative (
		final double cumulative)
		throws Exception
	{
		if (!NumberUtil.IsValid (cumulative) || 0. > cumulative || 1. < cumulative) {
			throw new Exception ("R1BernoulliDistribution::invCumulative => Invalid Inputs");
		}

		return 1. - _p > cumulative ? 0. : 1.;
	}

	/**
	 * Retrieve the Mean of the Distribution
	 * 
	 * @return The Mean of the Distribution
	 * 
	 * @throws Exception Thrown if the Mean cannot be estimated
	 */

	public double mean()
		throws Exception
	{
		return _p;
	}

	/**
	 * Retrieve the Median of the Distribution
	 * 
	 * @return The Median of the Distribution
	 * 
	 * @throws Exception Thrown if the Median cannot be estimated
	 */

	public double median()
		throws Exception
	{
		return 0.5 > _p ? 0. : 0.5 < _p ? 1. : 0.;
	}

	/**
	 * Retrieve the Mode of the Distribution
	 * 
	 * @return The Mode of the Distribution
	 * 
	 * @throws Exception Thrown if the Median cannot be estimated
	 */

	public double mode()
		throws Exception
	{
		if (0.5 == _p) {
			return Double.NaN;
		}

		return 0.5 > _p ? 0. : 1.;
	}

	/**
	 * Retrieve the Variance of the Distribution
	 * 
	 * @return The Variance of the Distribution
	 * 
	 * @throws Exception Thrown if the Variance cannot be estimated
	 */

	public double variance()
		throws Exception
	{
		return _p * (1. - _p);
	}

	/**
	 * Retrieve the Skewness of the Distribution
	 * 
	 * @return The Skewness of the Distribution
	 * 
	 * @throws Exception Thrown if the Skewness cannot be estimated
	 */

	public double skewness()
		throws Exception
	{
		return (1. - 2. * _p) / Math.sqrt (_p * (1. - _p));
	}

	/**
	 * Retrieve the Excess Kurtosis of the Distribution
	 * 
	 * @return The Excess Kurtosis of the Distribution
	 * 
	 * @throws Exception Thrown if the Skewness cannot be estimated
	 */

	public double excessKurtosis()
		throws Exception
	{
		return (1. - 6. * _p * (1. - _p)) / (_p * (1. - _p));
	}

	/**
	 * Retrieve the Differential Entropy of the Distribution
	 * 
	 * @return The Differential Entropy of the Distribution
	 * 
	 * @throws Exception Thrown if the Entropy cannot be estimated
	 */

	public double differentialEntropy()
		throws Exception
	{
		double q = 1. - _p;

		return -1. * _p * Math.log (_p) - q * Math.log (q);
	}

	/**
	 * Construct the Probability Generating Function
	 * 
	 * @return The Probability Generating Function
	 */

	public R1ToR1 probabilityGeneratingFunction()
	{
		return new R1ToR1 (null)
		{
			@Override public double evaluate (
				final double z)
				throws Exception
			{
				return 1. - _p + _p * z;
			}
		};
	}

	/**
	 * Retrieve the Fisher Information of the Distribution
	 * 
	 * @return The Fisher Information of the Distribution
	 * 
	 * @throws Exception Thrown if the Fisher Information cannot be estimated
	 */

	public double fisherInformation()
		throws Exception
	{
		return 1. / (_p * (1. - _p));
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
			throw new Exception ("R1BernoulliDistribution::nonCentralMoment - Invalid Moment Index");
		}

		return mean();
	}

	/**
	 * Retrieve the n<sup>th</sup> Central Moment
	 * 
	 * @param momentIndex Central Moment Index
	 * 
	 * @return n<sup>th</sup> Central Moment
	 * 
	 * @throws Exception Thrown if the Central Moment cannot be calculated
	 */

	public double centralMoment (
		final int momentIndex)
		throws Exception
	{
		if (0 >= momentIndex) {
			throw new Exception ("R1BernoulliDistribution::centralMoment - Invalid Moment Index");
		}

		double q = 1. - _p;

		return q * Math.pow (-1. * _p, momentIndex) + _p * Math.pow (q, momentIndex);
	}

	/**
	 * Retrieve the Second Central Moment
	 * 
	 * @return Second Central Moment
	 */

	public double secondCentralMoment()
	{
		return _p * (1. - _p);
	}

	/**
	 * Retrieve the Third Central Moment
	 * 
	 * @return Third Central Moment
	 */

	public double thirdCentralMoment()
	{
		return _p * (1. - _p) * (1. - 2. * _p);
	}

	/**
	 * Retrieve the Fourth Central Moment
	 * 
	 * @return Fourth Central Moment
	 */

	public double fourthCentralMoment()
	{
		double oneMinusP = 1. - _p;
		return _p * oneMinusP * (1. - 3. * _p * oneMinusP);
	}

	/**
	 * Retrieve the Fifth Central Moment
	 * 
	 * @return Fifth Central Moment
	 */

	public double fifthCentralMoment()
	{
		double oneMinusP = 1. - _p;
		return _p * oneMinusP * (1. - 2. * _p) * (1. - 2. * _p * oneMinusP);
	}

	/**
	 * Retrieve the Sixth Central Moment
	 * 
	 * @return Sixth Central Moment
	 */

	public double sixthCentralMoment()
	{
		double oneMinusP = 1. - _p;
		return _p * oneMinusP * (1. - 5. * _p * oneMinusP * (1. - _p * oneMinusP));
	}

	/**
	 * Retrieve the Fourth Central Moment Using the First and the Second Moments
	 * 
	 * @return Fourth Central Moment Using the First and the Second Moments
	 */

	public double fourthCentralMomentUsingFirstSecond()
	{
		double secondMoment = secondCentralMoment();

		return secondMoment * (1. - 3. * secondMoment);
	}

	/**
	 * Retrieve the Fifth Central Moment Using the First and the Second Moments
	 * 
	 * @return Fifth Central Moment Using the First and the Second Moments
	 */

	public double fifthCentralMomentUsingFirstSecond()
	{
		return thirdCentralMoment() * (1. - 2. * secondCentralMoment());
	}

	/**
	 * Retrieve the Sixth Central Moment Using the First and the Second Moments
	 * 
	 * @return Sixth Central Moment Using the First and the Second Moments
	 */

	public double sixthCentralMomentUsingFirstSecond()
	{
		double secondMoment = secondCentralMoment();

		return secondMoment * (1. - 5. * secondMoment * (1. - secondMoment));
	}

	/**
	 * Retrieve the First Cumulant
	 * 
	 * @return First Cumulant
	 */

	public double firstCumulant()
	{
		return _p;
	}

	/**
	 * Retrieve the Second Cumulant
	 * 
	 * @return Second Cumulant
	 */

	public double secondCumulant()
	{
		return secondCentralMoment();
	}

	/**
	 * Retrieve the Third Cumulant
	 * 
	 * @return Third Cumulant
	 */

	public double thirdCumulant()
	{
		return thirdCentralMoment();
	}

	/**
	 * Retrieve the Fourth Cumulant
	 * 
	 * @return Fourth Cumulant
	 */

	public double fourthCumulant()
	{
		double secondCentralMoment = secondCentralMoment();

		return secondCentralMoment * (1. - 6. * secondCentralMoment);
	}

	/**
	 * Retrieve the Fifth Cumulant
	 * 
	 * @return Fifth Cumulant
	 */

	public double fifthCumulant()
	{
		return thirdCentralMoment() * (1. - 12. * secondCentralMoment());
	}

	/**
	 * Retrieve the Sixth Cumulant
	 * 
	 * @return Sixth Cumulant
	 */

	public double sixthCumulant()
	{
		double secondCentralMoment = secondCentralMoment();

		return secondCentralMoment * (1. - 30. * secondCentralMoment * (1. - 4. * secondCentralMoment));
	}
}
