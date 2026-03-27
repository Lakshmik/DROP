
package org.drip.sample.r1pdfmetrics;

import org.drip.measure.generators.R1BernoulliMGF;
import org.drip.measure.pdf.R1BernoulliDistribution;
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
 * <i>BernoulliDistributionAnalyzer</i> illustrates the Invocation and the Usage of Bernoulli Probability
 * 	Distribution Function. The References are:
 * 
 * <br>
 * 	<ul>
 * 		<li>
 * 			Bertsekas, D., and J. Tsitsiklis (2002): <i>Introduction to Probability</i> <b>Athena
 * 				Scientific</b> Belmont MA
 * 		</li>
 * 		<li>
 * 			Dekking, F., C. Kraaikamp, H. Lopuhaa, and L. Meester (2010): <i>A Modern Introduction to
 * 				Probability and Statistics 1<sup>st</sup> Edition</i> <b>Springer</b> London UK
 * 		</li>
 * 		<li>
 * 			McCullagh, P., and J. Nelder (1989): <i>Generalized Linear Models 2<sup>nd</sup> Edition</i>
 * 				<b>Chapman and Hall/CRC</b> Boca Raton FL
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
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<li><b>Module </b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></li>
 *		<li><b>Library</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ModelValidationAnalyticsLibrary.md">Model Validation Analytics Library</a></li>
 *		<li><b>Project</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></li>
 *		<li><b>Package</b> = <a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/sample/r1pdfmetrics/README.md">R<sup>1</sup> Probability Distribution Function Metrics</a></li>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class BernoulliDistributionAnalyzer
{

	private static final void Analyze (
		final double p,
		final double[] xArray)
		throws Exception
	{
		R1BernoulliDistribution r1BernoulliDistribution = new R1BernoulliDistribution (p);

		double[] leftRightSupportArray = r1BernoulliDistribution.support();

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t|                BERNOULLI DISTRIBUTION METRICS                 ||");

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println ("\t| p                                      => " + p);

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println (
			"\t| Support                                => {" +
				leftRightSupportArray[0] + " | " + leftRightSupportArray[1] + "}"
		);

		String dump = "\t| Supported?                             => {";

		for (double x : xArray) {
			dump += " " + r1BernoulliDistribution.supported (x) + " |";
		}

		System.out.println (dump + "}");

		dump = "\t| Density                                => {";

		for (double x : xArray) {
			dump += FormatUtil.FormatDouble (r1BernoulliDistribution.density (x), 1, 2, 1.) + " |";
		}

		System.out.println (dump + "}");

		dump = "\t| Cumulative                             => {";

		for (double x : xArray) {
			dump += FormatUtil.FormatDouble (r1BernoulliDistribution.cumulative (x), 1, 2, 1.) + " |";
		}

		System.out.println (dump + "}");

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println (
			"\t| Mean                                   => " + r1BernoulliDistribution.mean()
		);

		System.out.println (
			"\t| Median                                 => " + r1BernoulliDistribution.median()
		);

		System.out.println (
			"\t| Mode                                   => " + r1BernoulliDistribution.mode()
		);

		System.out.println (
			"\t| Variance                               => " + r1BernoulliDistribution.variance()
		);

		System.out.println (
			"\t| Skewness                               => " + r1BernoulliDistribution.skewness()
		);

		System.out.println (
			"\t| Excess Kurtosis                        => " + r1BernoulliDistribution.excessKurtosis()
		);

		System.out.println (
			"\t| Differential Entropy                   => " + r1BernoulliDistribution.differentialEntropy()
		);

		System.out.println (
			"\t| Fisher Information                     => " + r1BernoulliDistribution.fisherInformation()
		);

		System.out.println ("\t|---------------------------------------------------------------||");

		R1BernoulliMGF r1BernoulliMGF = new R1BernoulliMGF (r1BernoulliDistribution);

		System.out.println ("\t| Mean (From MGF)                        => " + r1BernoulliMGF.mean());

		System.out.println ("\t| Variance (From MGF)                    => " + r1BernoulliMGF.variance());

		System.out.println (
			"\t| 2nd Non-central Moment (From MGF)      => " + r1BernoulliMGF.nonCentralMoment (2)
		);

		System.out.println (
			"\t| 3rd Non-central Moment (From MGF)      => " + r1BernoulliMGF.nonCentralMoment (3)
		);

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println (
			"\t| 2nd Central Moment                     => " + r1BernoulliDistribution.centralMoment (2)
		);

		System.out.println (
			"\t| 2nd Central Moment (Explicit)          => " + r1BernoulliDistribution.secondCentralMoment()
		);

		System.out.println (
			"\t| 3rd Central Moment                     => " + r1BernoulliDistribution.centralMoment (3)
		);

		System.out.println (
			"\t| 3rd Central Moment (Explicit)          => " + r1BernoulliDistribution.thirdCentralMoment()
		);

		System.out.println (
			"\t| 4th Central Moment                     => " + r1BernoulliDistribution.centralMoment (4)
		);

		System.out.println (
			"\t| 4th Central Moment (Explicit)          => " + r1BernoulliDistribution.fourthCentralMoment()
		);

		System.out.println (
			"\t| 4th Central Moment (Using 1st and 2nd) => " +
				r1BernoulliDistribution.fourthCentralMomentUsingFirstSecond()
		);

		System.out.println (
			"\t| 5th Central Moment                     => " + r1BernoulliDistribution.centralMoment (5)
		);

		System.out.println (
			"\t| 5th Central Moment (Explicit)          => " + r1BernoulliDistribution.fifthCentralMoment()
		);

		System.out.println (
			"\t| 5th Central Moment (Using 1st and 2nd) => " +
				r1BernoulliDistribution.fifthCentralMomentUsingFirstSecond()
		);

		System.out.println (
			"\t| 6th Central Moment                     => " + r1BernoulliDistribution.centralMoment (6)
		);

		System.out.println (
			"\t| 6th Central Moment (Explicit)          => " + r1BernoulliDistribution.sixthCentralMoment()
		);

		System.out.println (
			"\t| 6th Central Moment (Using 1st and 2nd) => " +
				r1BernoulliDistribution.sixthCentralMomentUsingFirstSecond()
		);

		System.out.println ("\t|---------------------------------------------------------------||");

		System.out.println (
			"\t| First Cumulant                         => " + r1BernoulliDistribution.firstCumulant()
		);

		System.out.println (
			"\t| Second Cumulant                        => " + r1BernoulliDistribution.secondCumulant()
		);

		System.out.println (
			"\t| Third Cumulant                         => " + r1BernoulliDistribution.thirdCumulant()
		);

		System.out.println (
			"\t| Fourth Cumulant                        => " + r1BernoulliDistribution.fourthCumulant()
		);

		System.out.println (
			"\t| Fifth Cumulant                         => " + r1BernoulliDistribution.fifthCumulant()
		);

		System.out.println (
			"\t| Sixth Cumulant                         => " + r1BernoulliDistribution.sixthCumulant()
		);

		System.out.println ("\t|---------------------------------------------------------------||\n");
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

		double[] pArray = new double[]
		{
			0.00,
			0.25,
			0.50,
			0.75,
			1.00
		};
		double[] xArray = new double[]
		{
			0.00,
			0.25,
			0.50,
			0.75,
			1.00
		};

		for (double p : pArray) {
			Analyze (p, xArray);
		}

		EnvManager.TerminateEnv();
	}
}
