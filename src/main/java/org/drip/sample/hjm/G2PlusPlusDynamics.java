
package org.drip.sample.hjm;

import org.drip.analytics.date.*;
import org.drip.dynamics.hjm.G2PlusPlus;
import org.drip.function.r1tor1operator.Flat;
import org.drip.sequence.random.*;
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
 * Copyright (C) 2015 Lakshmi Krishnamurthy
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
 * <i>G2PlusPlusDynamics</i> demonstrates the Construction and Usage of the G2++ 2-Factor HJM Model Dynamics
 * 	for the Evolution of the Short Rate.
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/README.md">DROP API Construction and Usage</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/sample/hjm/README.md">HJM Multi-Factor Principal Dynamics</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class G2PlusPlusDynamics
{

	private static final G2PlusPlus G2PlusPlusEvolver (
		final double sigma,
		final double a,
		final double eta,
		final double b,
		final double rho,
		final double startingForwardRate)
		throws Exception
	{
		return new G2PlusPlus (
			sigma,
			a,
			eta,
			b,
			new UnivariateSequenceGenerator[] {
				new BoxMullerGaussian (0., 1.),
				new BoxMullerGaussian (0., 1.)
			},
			rho,
			new Flat (startingForwardRate)
		);
	}

	private static final void ShortRateEvolution (
		final G2PlusPlus g2PlusPlus,
		final JulianDate startDate,
		final String currency,
		final String viewTenor,
		final double dblStartingShortRate)
		throws Exception
	{
		double x = 0.;
		double y = 0.;
		int dayStep = 2;
		JulianDate spotDate = startDate;
		double shortRate = dblStartingShortRate;

		int startDateJulian = startDate.julian();

		int endDateJulian = startDate.addTenor (viewTenor).julian();

		System.out.println ("\t|-----------------------------------------------------------------------||");

		System.out.println ("\t|                                                                       ||");

		System.out.println ("\t|         G2++ - 2-factor HJM Model - Short Rate Evolution Run          ||");

		System.out.println ("\t|-----------------------------------------------------------------------||");

		System.out.println ("\t|                                                                       ||");

		System.out.println ("\t|    L->R:                                                              ||");

		System.out.println ("\t|        Date                                                           ||");

		System.out.println ("\t|        X (%)                                                          ||");

		System.out.println ("\t|        X - Increment (%)                                              ||");

		System.out.println ("\t|        Y (%)                                                          ||");

		System.out.println ("\t|        Y - Increment (%)                                              ||");

		System.out.println ("\t|        Phi (%)                                                        ||");

		System.out.println ("\t|        Short Rate (%)                                                 ||");

		System.out.println ("\t|-----------------------------------------------------------------------||");

		while (spotDate.julian() < endDateJulian) {
			int spotDateJulian = spotDate.julian();

			double deltaX = g2PlusPlus.deltaX (startDateJulian, spotDateJulian, x, dayStep);

			x += deltaX;

			double deltaY = g2PlusPlus.deltaY (startDateJulian, spotDateJulian, y, dayStep);

			y += deltaY;

			double phi = g2PlusPlus.phi (startDateJulian, spotDateJulian);

			shortRate = x + y + phi;

			System.out.println ("\t| [" + spotDate + "] = " +
				FormatUtil.FormatDouble (x, 1, 2, 100.) + "% | " +
				FormatUtil.FormatDouble (deltaX, 1, 2, 100.) + "% || " +
				FormatUtil.FormatDouble (y, 1, 2, 100.) + "% | " +
				FormatUtil.FormatDouble (deltaY, 1, 2, 100.) + "% || " +
				FormatUtil.FormatDouble (phi, 1, 2, 100.) + "% || " +
				FormatUtil.FormatDouble (shortRate, 1, 2, 100.) + "% || "
			);

			spotDate = spotDate.addBusDays (dayStep, currency);
		}

		System.out.println ("\t|-----------------------------------------------------------------------||");
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

		JulianDate spotDate = DateUtil.Today();

		double startingShortRate = 0.05;
		String currency = "USD";
		double sigma = 0.05;
		double eta = 0.05;
		double rho = 0.5;
		double b = 0.5;
		double a = 0.5;

		ShortRateEvolution (
			G2PlusPlusEvolver (sigma, a, eta, b, rho, startingShortRate),
			spotDate,
			currency,
			"4M",
			startingShortRate
		);

		EnvManager.TerminateEnv();
	}
}
