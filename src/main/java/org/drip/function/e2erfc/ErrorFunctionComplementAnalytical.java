
package org.drip.function.e2erfc;

import org.drip.function.definition.R1ToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.numerical.estimation.R1Estimate;
import org.drip.numerical.r1integration.NewtonCotesQuadratureGenerator;

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
 * <i>ErrorFunctionComplementAnalytical</i> implements Analytical Versions of the Error Function Complement
 * 	(erfc) Estimate. The References are:
 * 
 * <br><br>
 * 	<ul>
 * 		<li>
 * 			Abramowitz, M., and I. A. Stegun (2007): <i>Handbook of Mathematics Functions</i> <b>Dover Book
 * 				on Mathematics</b>
 * 		</li>
 * 		<li>
 * 			Chang, S. H., P. C. Cosman, L. B. Milstein (2011): Chernoff-Type Bounds for Gaussian Error
 * 				Function <i>IEEE Transactions on Communications</i> <b>59 (11)</b> 2939-2944
 * 		</li>
 * 		<li>
 * 			Cody, W. J. (1991): Algorithm 715: SPECFUN – A Portable FORTRAN Package of Special Function
 * 				Routines and Test Drivers <i>ACM Transactions on Mathematical Software</i> <b>19 (1)</b>
 * 				22-32
 * 		</li>
 * 		<li>
 * 			Schopf, H. M., and P. H. Supancic (2014): On Burmann’s Theorem and its Application to Problems of
 * 				Linear and Non-linear Heat Transfer and Diffusion
 * 				https://www.mathematica-journal.com/2014/11/on-burmanns-theorem-and-its-application-to-problems-of-linear-and-nonlinear-heat-transfer-and-diffusion/#more-39602/
 * 		</li>
 * 		<li>
 * 			Wikipedia (2019): Error Function https://en.wikipedia.org/wiki/Error_function
 * 		</li>
 * 	</ul>
 *  
 * It provides the following Functions:
 *
 *  <ul>
 * 		<li>Construct Karagiannidis-Lioumpas (2007a) Version of the Analytical Error Function Complement</li>
 * 		<li>Construct Karagiannidis-Lioumpas (2007b) Version of the Analytical Error Function Complement</li>
 * 		<li>Construct the Chiani-Dardari-Simon (2012a) Version of the Analytical Error Function Complement</li>
 * 		<li>Construct the Chiani-Dardari-Simon (2012b) Version of the Analytical Error Function Complement</li>
 * 		<li>Construct the Chang-Cosman-Milstein (2011) Version of the Analytical Error Function Complement</li>
 * 		<li>Construct the Continued Fraction Expansion Version of the Analytical Error Function Complement</li>
 * 		<li>Construct the Craig 1991 Version of the ErrorFunctionComplement Quadrature</li>
 *  </ul>
 * 
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/e2erfc/README.md">E<sub>2</sub> erfc Estimation Function Implementation</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class ErrorFunctionComplementAnalytical
{

	private static final double ContinuedFractionRecursor (
		final double z,
		final int termIndex,
		final int termCount)
	{
		if (termIndex == termCount) {
			return 0.;
		}

		return ((1 == termIndex % 2) ? z * z : 1.) + 0.5 * termIndex /
			(1. + ContinuedFractionRecursor (z, termIndex + 1, termCount)
		);
	}

	/**
	 * Construct Karagiannidis-Lioumpas (2007a) Version of the Analytical Error Function Complement
	 * 
	 * @param A A
	 * @param B B
	 * 
	 * @return Karagiannidis-Lioumpas (2007) Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement KaragiannidisLioumpas2007 (
		final double A,
		final double B)
	{
		try {
			return !NumberUtil.IsValid (A) || !NumberUtil.IsValid (B) ?
				null : new ErrorFunctionComplement (null, null)
				{
					@Override public double evaluate (
						final double z)
						throws Exception
					{
						if (!NumberUtil.IsValid (z)) {
							throw new Exception (
								"ErrorFunctionComplementAnalytical::KaragiannidisLioumpas2007::evaluate => Invalid Inputs"
							);
						}

						if (0. == z) {
							return 1.;
						}

						if (z < 0) {
							return 2. - evaluate (-1. * z);
						}

						return (1. - Math.exp (-1. * A * z)) * Math.exp (-1. * z * z) /
							(B * z * Math.sqrt (Math.PI));
					}
				};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct Karagiannidis-Lioumpas (2007b) Version of the Analytical Error Function Complement
	 * 
	 * @return Karagiannidis-Lioumpas (2007b) Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement KaragiannidisLioumpas2007()
	{
		return KaragiannidisLioumpas2007 (1.980, 1.135);
	}

	/**
	 * Construct the Chiani-Dardari-Simon (2012a) Version of the Analytical Error Function Complement
	 * 
	 * @return The Chiani-Dardari-Simon (2012a) Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement ChianiDardariSimon2012a()
	{
		try {
			return new ErrorFunctionComplement (null, null)
			{
				@Override public double evaluate (
					final double z)
					throws Exception
				{
					if (!NumberUtil.IsValid (z)) {
						throw new Exception (
							"ErrorFunctionComplementAnalytical::ChianiDardariSimon2012a::evaluate => Invalid Inputs"
						);
					}

					if (0. == z) {
						return 1.;
					}

					if (z < 0) {
						return 2. - evaluate (-1. * z);
					}

					return 0.5 * Math.exp (-2. * z * z) + 0.5 * Math.exp (-1. * z * z);
				}

				@Override public R1Estimate boundedEstimate (
					final double z)
				{
					try {
						double baseline = evaluate (z);

						return new R1Estimate (baseline, baseline, Math.exp (-1. * z * z));
					} catch (Exception e) {
						e.printStackTrace();
					}

					return null;
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Chiani-Dardari-Simon (2012b) Version of the Analytical Error Function Complement
	 * 
	 * @return The Chiani-Dardari-Simon (2012b) Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement ChianiDardariSimon2012b()
	{
		try {
			return new ErrorFunctionComplement (null, null)
			{
				@Override public double evaluate (
					final double z)
					throws Exception
				{
					if (!NumberUtil.IsValid (z)) {
						throw new Exception (
							"ErrorFunctionComplementAnalytical::ChianiDardariSimon2012b::evaluate => Invalid Inputs"
						);
					}

					if (0. == z) {
						return 1.;
					}

					if (z < 0) {
						return 2. - evaluate (-1. * z);
					}

					return Math.exp (-1. * z * z) / 6. + 0.5 * Math.exp (-4. * z * z / 3.);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Chang-Cosman-Milstein (2011) Version of the Analytical Error Function Complement
	 * 
	 * @param beta Beta
	 * 
	 * @return The Chang-Cosman-Milstein (2011) Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement ChangCosmanMilstein2011 (
		final double beta)
	{
		try {
			return !NumberUtil.IsValid (beta) || 1. >= beta ? null : new ErrorFunctionComplement (null, null)
			{
				@Override public double evaluate (
					final double z)
					throws Exception
				{
					if (!NumberUtil.IsValid (z)) {
						throw new Exception (
							"ErrorFunctionComplementAnalytical::ChangCosmanMilstein2011::evaluate => Invalid Inputs"
						);
					}

					if (0. == z) {
						return 1.;
					}

					if (z < 0) {
						return 2. - evaluate (-1. * z);
					}

					return Math.sqrt (2. * Math.E * (beta - 1.) / Math.PI) * Math.exp (-1. * beta * z * z) /
						beta;
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Continued Fraction Expansion Version of the Analytical Error Function Complement
	 * 
	 * @param termCount Term Count
	 * 
	 * @return The Continued Fraction Expansion Version of the Analytical Error Function Complement
	 */

	public static final ErrorFunctionComplement ContinuedFractionExpansion (
		final int termCount)
	{
		try {
			return 0 >= termCount ? null : new ErrorFunctionComplement (null, null)
			{
				@Override public double evaluate (
					final double z)
					throws Exception
				{
					if (!NumberUtil.IsValid (z)) {
						throw new Exception (
							"ErrorFunctionComplementAnalytical::ContinuedFractionExpansion::evaluate => Invalid Inputs"
						);
					}

					if (0. == z) {
						return 1.;
					}

					if (z < 0) {
						return 2. - evaluate (-1. * z);
					}

					return z * Math.exp (-1. * z * z) / Math.sqrt (Math.PI) /
						ContinuedFractionRecursor (z, 1, termCount);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct the Craig 1991 Version of the ErrorFunctionComplement Quadrature
	 * 
	 * @return The Craig 1991 Version of the ErrorFunctionComplement Quadrature
	 */

	public static final ErrorFunctionComplement Craig1991()
	{
		try {
			return new ErrorFunctionComplement (null, null)
			{
				@Override public double evaluate (
					final double z)
					throws Exception
				{
					if (!NumberUtil.IsValid (z)) {
						throw new Exception (
							"ErrorFunctionComplementAnalytical::Craig1991::evaluate => Invalid Inputs"
						);
					}

					if (0. == z) {
						return 1.;
					}

					if (z < 0) {
						return 2. - evaluate (-1. * z);
					}

					return NewtonCotesQuadratureGenerator.Zero_PlusOne (0., z, 100).integrate (
						new R1ToR1 (null)
						{
							@Override public double evaluate (
								final double theta)
								throws Exception
							{
								if (0. == theta) {
									return 0.;
								}

								double sinTheta = Math.sin (theta);

								return 2. * Math.exp (-1. * z * z / (sinTheta * sinTheta)) / Math.PI;
							}
						}
					);
				}
			};
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
