
package org.drip.dynamics.hullwhite;

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
 * <i>TrinomialTreeTransitionMetrics</i> records the Transition Metrics associated with Node-to-Node
 * 	Evolution of the Instantaneous Short Rate using the Hull-White Model Trinomial Tree. It provides the
 * 	following Functions:
 *
 *  <ul>
 * 		<li><i>TrinomialTreeTransitionMetrics</i> Constructor</li>
 * 		<li>Retrieve the Initial Date</li>
 * 		<li>Retrieve the Terminal Date</li>
 * 		<li>Retrieve the Tree Time Index</li>
 * 		<li>Retrieve the Expected Final/Terminal Value for X</li>
 * 		<li>Retrieve the Variance in the Final Value of X</li>
 * 		<li>Retrieve the Stochastic Shift of X</li>
 * 		<li>Retrieve the Tree Stochastic Displacement Index</li>
 * 		<li>Retrieve the Probability of the Up Stochastic Shift</li>
 * 		<li>Retrieve the Probability of the Down Stochastic Shift</li>
 * 		<li>Retrieve the Probability of the No Shift</li>
 * 		<li>Retrieve the "Up" Value for X</li>
 * 		<li>Retrieve the "Down" Value for X</li>
 * 		<li>Retrieve the Final/Terminal Alpha</li>
 * 		<li>Retrieve the "Up" Node Metrics</li>
 * 		<li>Retrieve the "Down" Node Metrics</li>
 * 		<li>Retrieve the "Stay" Node Metrics</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/hullwhite/README.md">Hull White Latent State Evolution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class TrinomialTreeTransitionMetrics
{
	private long _treeTimeIndex = -1L;
	private double _xVariance = Double.NaN;
	private double _probabilityUp = Double.NaN;
	private double _terminalAlpha = Double.NaN;
	private long _treeStochasticBaseIndex = -1L;
	private double _probabilityDown = Double.NaN;
	private double _probabilityStay = Double.NaN;
	private int _initialDate = Integer.MIN_VALUE;
	private double _xStochasticShift = Double.NaN;
	private int _terminalDate = Integer.MIN_VALUE;
	private double _expectedTerminalX = Double.NaN;
	private long _treeStochasticDisplacementIndex = -1L;

	/**
	 * <i>TrinomialTreeTransitionMetrics</i> Constructor
	 * 
	 * @param initialDate The Initial Date
	 * @param terminalDate The Terminal/Final Date
	 * @param treeTimeIndex The Tree Time Index
	 * @param treeStochasticBaseIndex The Tree Stochastic Base Index
	 * @param expectedTerminalX Expectation of the Final/Terminal Value for X
	 * @param xVariance Variance of X
	 * @param terminalAlpha The Final/Terminal Alpha
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public TrinomialTreeTransitionMetrics (
		final int initialDate,
		final int terminalDate,
		final long treeTimeIndex,
		final long treeStochasticBaseIndex,
		final double expectedTerminalX,
		final double xVariance,
		final double terminalAlpha)
		throws Exception
	{
		if (0 > (_treeTimeIndex = treeTimeIndex) ||
			!NumberUtil.IsValid (_expectedTerminalX = expectedTerminalX) ||
			!NumberUtil.IsValid (_xVariance = xVariance) ||
			!NumberUtil.IsValid (_terminalAlpha = terminalAlpha))
		{
			throw new Exception ("TrinomialTreeTransitionMetrics Constructor: Invalid Inputs");
		}

		_treeStochasticDisplacementIndex = Math.round (
			_expectedTerminalX / (_xStochasticShift = Math.sqrt (_xVariance * 3.))
		);

		_initialDate = initialDate;
		_terminalDate = terminalDate;
		_treeStochasticBaseIndex = treeStochasticBaseIndex;
		double eta = _expectedTerminalX - _treeStochasticDisplacementIndex * _xStochasticShift;
		_probabilityDown = (1. / 6.) + (eta * eta / (6. * _xVariance)) - (0.5 * eta / _xStochasticShift);
		_probabilityUp = (1. / 6.) + (eta * eta / (6. * _xVariance)) + (0.5 * eta / _xStochasticShift);
		_probabilityStay = (2. / 3.) - (eta * eta / (3. * _xVariance));
	}

	/**
	 * Retrieve the Initial Date
	 * 
	 * @return The Initial Date
	 */

	public int initialDate()
	{
		return _initialDate;
	}

	/**
	 * Retrieve the Terminal Date
	 * 
	 * @return The Terminal Date
	 */

	public int terminalDate()
	{
		return _terminalDate;
	}

	/**
	 * Retrieve the Tree Time Index
	 * 
	 * @return The Tree Time Index
	 */

	public long treeTimeIndex()
	{
		return _treeTimeIndex;
	}

	/**
	 * Retrieve the Expected Final/Terminal Value for X
	 * 
	 * @return The Expected Final/Terminal Value for X
	 */

	public double expectedTerminalX()
	{
		return _expectedTerminalX;
	}

	/**
	 * Retrieve the Variance in the Final Value of X
	 * 
	 * @return The Variance in the Final Value of X
	 */

	public double xVariance()
	{
		return _xVariance;
	}

	/**
	 * Retrieve the Stochastic Shift of X
	 * 
	 * @return The Stochastic Shift of X
	 */

	public double xStochasticShift()
	{
		return _xStochasticShift;
	}

	/**
	 * Retrieve the Tree Stochastic Displacement Index
	 * 
	 * @return The Tree Stochastic Displacement Index
	 */

	public long treeStochasticDisplacementIndex()
	{
		return _treeStochasticDisplacementIndex;
	}

	/**
	 * Retrieve the Probability of the Up Stochastic Shift
	 * 
	 * @return Probability of the Up Stochastic Shift
	 */

	public double probabilityUp()
	{
		return _probabilityUp;
	}

	/**
	 * Retrieve the Probability of the Down Stochastic Shift
	 * 
	 * @return Probability of the Down Stochastic Shift
	 */

	public double probabilityDown()
	{
		return _probabilityDown;
	}

	/**
	 * Retrieve the Probability of the No Shift
	 * 
	 * @return Probability of the No Shift
	 */

	public double probabilityStay()
	{
		return _probabilityStay;
	}

	/**
	 * Retrieve the "Up" Value for X
	 * 
	 * @return The "Up" Value for X
	 */

	public double xUp()
	{
		return (_treeStochasticDisplacementIndex + 1) * _xStochasticShift;
	}

	/**
	 * Retrieve the "Down" Value for X
	 * 
	 * @return The "Down" Value for X
	 */

	public double xDown()
	{
		return (_treeStochasticDisplacementIndex - 1) * _xStochasticShift;
	}

	/**
	 * Retrieve the Final/Terminal Alpha
	 * 
	 * @return The Final/Terminal Alpha
	 */

	public double terminalAlpha()
	{
		return _terminalAlpha;
	}

	/**
	 * Retrieve the "Up" Node Metrics
	 * 
	 * @return The "Up" Node Metrics
	 */

	public TrinomialTreeNodeMetrics upNodeMetrics()
	{
		try {
			return new TrinomialTreeNodeMetrics (
				_treeTimeIndex,
				_treeStochasticBaseIndex + 1,
				(_treeStochasticDisplacementIndex + 1) * _xStochasticShift,
				_terminalAlpha
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the "Down" Node Metrics
	 * 
	 * @return The "Down" Node Metrics
	 */

	public TrinomialTreeNodeMetrics downNodeMetrics()
	{
		try {
			return new TrinomialTreeNodeMetrics (
				_treeTimeIndex,
				_treeStochasticBaseIndex - 1,
				(_treeStochasticDisplacementIndex - 1) * _xStochasticShift,
				_terminalAlpha
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Retrieve the "Stay" Node Metrics
	 * 
	 * @return The "Stay" Node Metrics
	 */

	public TrinomialTreeNodeMetrics stayNodeMetrics()
	{
		try {
			return new TrinomialTreeNodeMetrics (
				_treeTimeIndex,
				_treeStochasticBaseIndex,
				_treeStochasticDisplacementIndex * _xStochasticShift,
				_terminalAlpha
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
