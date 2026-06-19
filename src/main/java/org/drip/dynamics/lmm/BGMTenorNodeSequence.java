
package org.drip.dynamics.lmm;

import org.drip.analytics.date.JulianDate;
import org.drip.service.common.FormatUtil;

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
 * <i>BGMTenorNodeSequence</i> contains the Point Nodes of the Latent State Quantifiers and their Increments
 * 	present in the specified BGMForwardTenorSnap Instance. The References are:
 *
 *	<br><br>
 *  <ul>
 *  	<li>
 *  		Goldys, B., M. Musiela, and D. Sondermann (1994): <i>Log-normality of Rates and Term Structure
 *  			Models</i> <b>The University of New South Wales</b>
 *  	</li>
 *  	<li>
 *  		Musiela, M. (1994): <i>Nominal Annual Rates and Log-normal Volatility Structure</i> <b>The
 *  			University of New South Wales</b>
 *  	</li>
 *  	<li>
 * 			Brace, A., D. Gatarek, and M. Musiela (1997): The Market Model of Interest Rate Dynamics
 * 				<i>Mathematical Finance</i> <b>7 (2)</b> 127-155
 *  	</li>
 *  </ul>
 *  
 * It provides the following Functions:
 *
 *  <ul>
 * 		<li><i>BGMTenorNodeSequence</i> Constructor</li>
 * 		<li>Retrieve the Array of Tenor Dates</li>
 * 		<li>Retrieve the Array of Tenor LIBOR Rates</li>
 * 		<li>Retrieve the Array of Tenor LIBOR Rate Increments</li>
 * 		<li>Retrieve the Array of Tenor Discount Factors</li>
 * 		<li>Retrieve the Array of Tenor Discount Factor Increments</li>
 * 		<li>Retrieve the Array of Tenor Instantaneous Effective Annual Forward Rate</li>
 * 		<li>Retrieve the Array of Tenor Instantaneous Nominal Annual Forward Rate</li>
 * 		<li>Retrieve the Array of Tenor Instantaneous Continuously Compounded Forward Rate Increments</li>
 * 		<li>Retrieve the Array of Tenor Spot Rate Increments</li>
 * 		<li>JSON-ization of the Tenor Node Sequence</li>
 *	<br>
 *
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/lmm/README.md">LMM Based Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class BGMTenorNodeSequence
{
	private int[] _dateArray = null;
	private double[] _liborArray = null;
	private double[] _liborIncrementArray = null;
	private double[] _discountFactorArray = null;
	private double[] _spotRateIncrementArray = null;
	private double[] _discountFactorIncrementArray = null;
	private double[] _continuousForwardRateIncrementArray = null;
	private double[] _instantaneousNominalForwardRateArray = null;
	private double[] _instantaneousEffectiveForwardRateArray = null;

	/**
	 * <i>BGMTenorNodeSequence</i> Constructor
	 * 
	 * @param bgmForwardTenorSnapArray Array of the BGM Forward Tenor Snap Instances
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public BGMTenorNodeSequence (
		final BGMForwardTenorSnap[] bgmForwardTenorSnapArray)
		throws Exception
	{
		if (null == bgmForwardTenorSnapArray || 0 == bgmForwardTenorSnapArray.length) {
			throw new Exception ("BGMTenorNodeSequence Constructor: Invalid Inputs!");
		}

		_dateArray = new int[bgmForwardTenorSnapArray.length];
		_liborArray = new double[bgmForwardTenorSnapArray.length];
		_liborIncrementArray = new double[bgmForwardTenorSnapArray.length];
		_discountFactorArray = new double[bgmForwardTenorSnapArray.length];
		_spotRateIncrementArray = new double[bgmForwardTenorSnapArray.length];
		_discountFactorIncrementArray = new double[bgmForwardTenorSnapArray.length];
		_continuousForwardRateIncrementArray = new double[bgmForwardTenorSnapArray.length];
		_instantaneousNominalForwardRateArray = new double[bgmForwardTenorSnapArray.length];
		_instantaneousEffectiveForwardRateArray = new double[bgmForwardTenorSnapArray.length];

		for (int snapIndex = 0; snapIndex < bgmForwardTenorSnapArray.length; ++snapIndex) {
			_dateArray[snapIndex] = bgmForwardTenorSnapArray[snapIndex].date();

			_liborArray[snapIndex] = bgmForwardTenorSnapArray[snapIndex].libor();

			_liborIncrementArray[snapIndex] = bgmForwardTenorSnapArray[snapIndex].liborIncrement();

			_discountFactorArray[snapIndex] = bgmForwardTenorSnapArray[snapIndex].discountFactor();

			_spotRateIncrementArray[snapIndex] = bgmForwardTenorSnapArray[snapIndex].spotRateIncrement();

			_discountFactorIncrementArray[snapIndex] =
				bgmForwardTenorSnapArray[snapIndex].discountFactorIncrement();

			_instantaneousNominalForwardRateArray[snapIndex] =
				bgmForwardTenorSnapArray[snapIndex].instantaneousNominalForwardRate();

			_instantaneousEffectiveForwardRateArray[snapIndex] =
				bgmForwardTenorSnapArray[snapIndex].instantaneousEffectiveForwardRate();

			_continuousForwardRateIncrementArray[snapIndex] =
				bgmForwardTenorSnapArray[snapIndex].continuouslyCompoundedForwardIncrement();
		}
	}

	/**
	 * Retrieve the Array of Tenor Dates
	 * 
	 * @return The Array of Tenor Dates
	 */

	public int[] dateArray()
	{
		return _dateArray;
	}

	/**
	 * Retrieve the Array of Tenor LIBOR Rates
	 * 
	 * @return The Array of Tenor LIBOR Rates
	 */

	public double[] liborArray()
	{
		return _liborArray;
	}

	/**
	 * Retrieve the Array of Tenor LIBOR Rate Increments
	 * 
	 * @return The Array of Tenor LIBOR Rate Increments
	 */

	public double[] liborIncrementArray()
	{
		return _liborIncrementArray;
	}

	/**
	 * Retrieve the Array of Tenor Discount Factors
	 * 
	 * @return The Array of Tenor Discount Factors
	 */

	public double[] discountFactorArray()
	{
		return _discountFactorArray;
	}

	/**
	 * Retrieve the Array of Tenor Discount Factor Increments
	 * 
	 * @return The Array of Tenor Discount Factor Increments
	 */

	public double[] discountFactorIncrementArray()
	{
		return _discountFactorIncrementArray;
	}

	/**
	 * Retrieve the Array of Tenor Instantaneous Effective Annual Forward Rate
	 * 
	 * @return The Array of Tenor Instantaneous Effective Annual Forward Rate
	 */

	public double[] instantaneousEffectiveForwardRateArray()
	{
		return _instantaneousEffectiveForwardRateArray;
	}

	/**
	 * Retrieve the Array of Tenor Instantaneous Nominal Annual Forward Rate
	 * 
	 * @return The Array of Tenor Instantaneous Nominal Annual Forward Rate
	 */

	public double[] instantaneousNominalForwardRateArray()
	{
		return _instantaneousNominalForwardRateArray;
	}

	/**
	 * Retrieve the Array of Tenor Instantaneous Continuously Compounded Forward Rate Increments
	 * 
	 * @return The Array of Tenor Instantaneous Continuously Compounded Forward Rate Increments
	 */

	public double[] continuousForwardRateIncrementArray()
	{
		return _continuousForwardRateIncrementArray;
	}

	/**
	 * Retrieve the Array of Tenor Spot Rate Increments
	 * 
	 * @return The Array of Tenor Spot Rate Increments
	 */

	public double[] spotRateIncrementArray()
	{
		return _spotRateIncrementArray;
	}

	/**
	 * JSON-ization of the Tenor Node Sequence
	 * 
	 * @return JSON-ized of the Tenor Node Sequence
	 */

	@Override public String toString()
	{
		String dateDump = "\t |";
		String liborDump = "\t |";
		String partition = "\t |";
		String discountFactorDump = "\t |";
		String liborIncrementDump = "\t |";
		String spotRateIncrementDump = "\t |";
		String discountFactorIncrementDump = "\t |";
		String continuousForwardIncrementDump = "\t |";
		String instantaneousNominalForwardDump = "\t |";
		String instantaneousEffectiveForwardDump = "\t |";

		for (int dateIndex = 0; dateIndex < _dateArray.length; ++dateIndex) {
			partition += "-------------";

			dateDump += " " + new JulianDate (_dateArray[dateIndex]) + " |";

			liborDump += "  " + FormatUtil.FormatDouble (
				_liborArray[dateIndex],
				1,
				3,
				100.
			) + "%   |";

			liborIncrementDump += "    " + FormatUtil.FormatDouble (
				_liborIncrementArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";

			discountFactorDump += "  " + FormatUtil.FormatDouble (
				_discountFactorArray[dateIndex],
				2,
				3,
				100.
			) + "   |";

			discountFactorIncrementDump += "    " + FormatUtil.FormatDouble (
				_discountFactorIncrementArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";

			continuousForwardIncrementDump += "    " + FormatUtil.FormatDouble (
				_continuousForwardRateIncrementArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";

			spotRateIncrementDump += "    " + FormatUtil.FormatDouble (
				_spotRateIncrementArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";

			instantaneousEffectiveForwardDump += "    " + FormatUtil.FormatDouble (
				_instantaneousEffectiveForwardRateArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";

			instantaneousNominalForwardDump += "    " + FormatUtil.FormatDouble (
				_instantaneousNominalForwardRateArray[dateIndex],
				2,
				0,
				10000.
			) + "     |";
		}

		return "\n" + partition + "|\n" +
			dateDump + "|\n" +
			partition + "|\n" +
			liborDump + "|\n" +
			liborIncrementDump + "|\n" +
			discountFactorDump + "|\n" +
			discountFactorIncrementDump + "|\n" +
			continuousForwardIncrementDump + "|\n" +
			spotRateIncrementDump + "|\n" +
			instantaneousEffectiveForwardDump + "|\n" +
			instantaneousNominalForwardDump + "|\n" +
			partition + "|\n";
	}
}
