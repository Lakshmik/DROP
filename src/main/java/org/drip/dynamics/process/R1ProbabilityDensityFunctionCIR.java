
package org.drip.dynamics.process;

import org.drip.dynamics.ito.TimeR1Vertex;
import org.drip.dynamics.meanreverting.CKLSParameters;
import org.drip.numerical.common.NumberUtil;
import org.drip.specialfunction.definition.ModifiedBesselFirstKindEstimator;

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
 * <i>R1ProbabilityDensityFunctionCIR</i> exposes the R<sup>1</sup> Probability Density Function Evaluation
 * 	Equation for an Underlying CIR Process. The References are:
 *  
 * 	<br><br>
 *  <ul>
 * 		<li>
 * 			Bogoliubov, N. N., and D. P. Sankevich (1994): N. N. Bogoliubov and Statistical Mechanics
 * 				<i>Russian Mathematical Surveys</i> <b>49 (5)</b> 19-49
 * 		</li>
 * 		<li>
 * 			Holubec, V., K. Kroy, and S. Steffenoni (2019): Physically Consistent Numerical Solver for
 * 				Time-dependent Fokker-Planck Equations <i>Physical Review E</i> <b>99 (4)</b> 032117
 * 		</li>
 * 		<li>
 * 			Kadanoff, L. P. (2000): <i>Statistical Physics: Statics, Dynamics, and Re-normalization</i>
 * 				<b>World Scientific</b>
 * 		</li>
 * 		<li>
 * 			Ottinger, H. C. (1996): <i>Stochastic Processes in Polymeric Fluids</i> <b>Springer-Verlag</b>
 * 				Berlin-Heidelberg
 * 		</li>
 * 		<li>
 * 			Wikipedia (2019): Fokker-Planck Equation
 * 				https://en.wikipedia.org/wiki/Fokker%E2%80%93Planck_equation
 * 		</li>
 *  </ul>
 *
 * 	It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>R1ProbabilityDensityFunctionCIR</i> Constructor</li>
 * 		<li>Retrieve "q"</li>
 * 		<li>Retrieve the Starting Value for r</li>
 * 		<li>Retrieve the CKLS Parameters</li>
 * 		<li>Retrieve the Modified Bessel Estimator of the First Kind</li>
 * 		<li>Calculates the PDF Density Value</li>
 *	</ul>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/process/README.md">Ito-Dynamics Based Stochastic Process</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class R1ProbabilityDensityFunctionCIR
	extends R1ProbabilityDensityFunction
{
	private double _q = Double.NaN;
	private double _r0 = Double.NaN;
	private CKLSParameters _cklsParameters = null;
	private double _twoAOverSigmaSquared = Double.NaN;
	private ModifiedBesselFirstKindEstimator _modifiedBesselFirstKindEstimator = null;

	/**
	 * <i>R1ProbabilityDensityFunctionCIR</i> Constructor
	 * 
	 * @param r0 Starting Value for r
	 * @param cklsParameters The CKLS Parameters
	 * @param modifiedBesselFirstKindEstimator Modified Bessel Estimator of the First Kind
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public R1ProbabilityDensityFunctionCIR (
		final double r0,
		final CKLSParameters cklsParameters,
		final ModifiedBesselFirstKindEstimator modifiedBesselFirstKindEstimator)
		throws Exception
	{
		if (!NumberUtil.IsValid (_r0 = r0) ||
			null == (_cklsParameters = cklsParameters) ||
			null == (_modifiedBesselFirstKindEstimator = modifiedBesselFirstKindEstimator))
		{
			throw new Exception ("R1ProbabilityDensityFunctionCIR Constructor => Invalid Inputs");
		}

		double volatilityCoefficient = _cklsParameters.volatilityCoefficient();

		_q = _cklsParameters.meanReversionLevel() * (
			_twoAOverSigmaSquared =
				2. * _cklsParameters.meanReversionSpeed() / volatilityCoefficient / volatilityCoefficient
		) - 1.;
	}

	/**
	 * Retrieve "q"
	 * 
	 * @return "q"
	 */

	public double q()
	{
		return _q;
	}

	/**
	 * Retrieve the Starting Value for r
	 * 
	 * @return Starting Value for r
	 */

	public double r0()
	{
		return _r0;
	}

	/**
	 * Retrieve the CKLS Parameters
	 * 
	 * @return The CKLS Parameters
	 */

	public CKLSParameters cklsParameters()
	{
		return _cklsParameters;
	}

	/**
	 * Retrieve the Modified Bessel Estimator of the First Kind
	 * 
	 * @return The Modified Bessel Estimator of the First Kind
	 */

	public ModifiedBesselFirstKindEstimator modifiedBesselFirstKindEstimator()
	{
		return _modifiedBesselFirstKindEstimator;
	}

	/**
	 * Calculates the PDF Density Value
	 * 
	 * @param timeR1Vertex The R<sup>1</sup> Property Variate/Time Coordinate Vertex
	 * 
	 * @return The PDF Density Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	@Override public double density (
		final TimeR1Vertex timeR1Vertex)
		throws Exception
	{
		if (null == timeR1Vertex) {
			throw new Exception ("R1ProbabilityDensityFunctionCIR::density => Invalid Inputs");
		}

		double ePowerMinusAT = Math.exp (-1. * _cklsParameters.meanReversionSpeed() * timeR1Vertex.t());

		double c = _twoAOverSigmaSquared / (1. - ePowerMinusAT);
		double u = c * _r0 * ePowerMinusAT;

		double v = c * timeR1Vertex.x();

		return c * Math.exp (-1. * (u + v)) * Math.pow (u / v, 0.5 * _q) *
			_modifiedBesselFirstKindEstimator.bigI (_q, 2. * Math.sqrt (u * v));
	}
}
