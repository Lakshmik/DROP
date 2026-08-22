
package org.drip.function.r1tor1solver;

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
 * Copyright (C) 2014 Lakshmi Krishnamurthy
 * Copyright (C) 2013 Lakshmi Krishnamurthy
 * Copyright (C) 2012 Lakshmi Krishnamurthy
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
 * <i>InitializationHeuristics</i> implements several heuristics used to kick off the fixed point
 * 	bracketing/search process. The following custom heuristics are implemented as part of the heuristics
 *  based kick-off:
 * 	<br>
 * 	<ul>
 * 		<li>Custom Bracketing Control Parameters: Any of the standard bracketing control parameters can be
 * 			customized to kick-off the bracketing search.</li>
 * 		<li>Soft Left/Right Bracketing Hints: The left/right starting bracket edges are used as soft
 * 			bracketing initialization hints.</li>
 * 		<li>Soft Mid Bracketing Hint: A mid bracketing level is specified to indicate the soft bracketing
 * 			kick-off.</li>
 * 		<li>Hard Bracketing Floor/Ceiling: A pair of hard floor and ceiling limits are specified as a
 * 			constraint to the bracketing.</li>
 * 		<li>Hard Search Boundaries: A pair of hard left and right boundaries are specified to kick-off the
 * 			final fixed point search.</li>
 * 	</ul>
 * 	<br>
 * 	These heuristics are further interpreted and developed inside the ExecutionInitializer and the
 * 		<i>ExecutionControl</i> implementations. It exposes the following Functions:
 *
 *  <ul>
 * 		<li>Start bracket initialization from the Generic Bracket Initializer</li>
 * 		<li>Start bracket initialization from Pre-specified left/right edge hints</li>
 * 		<li>Start bracket initialization from Pre-specified Starting Mid Bracketing Variate</li>
 * 		<li>Restrict the bracket initialization to within the specified Floor and Ceiling</li>
 * 		<li>Start search from Pre-specified Hard Search Brackets</li>
 * 		<li>Start search from Custom Bracketing Control Parameters</li>
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from the hard search edges
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from the bracketing edge soft hints</li>
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from the bracketing mid hint</li>
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from the bracketing hard floor/ceiling</li>
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from Custom Bracketing Control Parameters</li>
 * 		<li>Construct an <i>InitializationHeuristics</i> Instance from the set of Heuristics Parameters</li>
 * 		<li>Retrieve the Determinant</li>
 * 		<li>Retrieve the Hard Left Search Start</li>
 * 		<li>Retrieve the Hard Right Search Start</li>
 * 		<li>Retrieve the Soft Bracket Start Mid</li>
 * 		<li>Retrieve the Soft Bracket Start Left</li>
 * 		<li>Retrieve the Hard Bracket Floor</li>
 * 		<li>Retrieve the Soft Bracket Start Right</li>
 * 		<li>Retrieve the Hard Bracket Ceiling</li>
 * 		<li>Retrieve the Custom <i>BracketingControlParams</i></li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/NumericalAnalysisLibrary.md">Numerical Analysis Library</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/README.md">R<sup>d</sup> To R<sup>d</sup> Function Analysis</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/function/r1tor1solver/README.md">Built-in R<sup>1</sup> To R<sup>1</sup> Solvers</a></td></tr>
 *  </table>
 *	<br>

 *
 * @author Lakshmi Krishnamurthy
 */

public class InitializationHeuristics
{

	/**
	 * Start bracket initialization from the Generic Bracket Initializer
	 */

	public static final int BRACKETING_GENERIC_BCP = 0;

	/**
	 * Start bracket initialization from Pre-specified left/right edge hints
	 */

	public static final int BRACKETING_EDGE_HINTS = 1;

	/**
	 * Start bracket initialization from Pre-specified Starting Mid Bracketing Variate
	 */

	public static final int BRACKETING_MID_HINT = 2;

	/**
	 * Restrict the bracket initialization to within the specified Floor and Ceiling
	 */

	public static final int BRACKETING_FLOOR_CEILING = 4;

	/**
	 * Start search from Pre-specified Hard Search Brackets
	 */

	public static final int SEARCH_HARD_BRACKETS = 8;

	/**
	 * Start search from Custom Bracketing Control Parameters
	 */

	public static final int BRACKETING_CUSTOM_BCP = 16;

	private double _bracketFloor = Double.NaN;
	private double _bracketCeiling = Double.NaN;
	private double _searchStartLeft = Double.NaN;
	private double _searchStartRight = Double.NaN;
	private double _startingBracketMid = Double.NaN;
	private double _startingBracketLeft = Double.NaN;
	private double _startingBracketRight = Double.NaN;
	private int _determinant = BRACKETING_GENERIC_BCP;
	private BracketingControlParams _customBracketingControlParams = null;

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from the hard search edges
	 * 
	 * @param searchStartLeft Search Start Left Edge
	 * @param searchStartRight Search Start Right Edge
	 * 
	 * @return <i>InitializationHeuristics</i> instance
	 */

	public static final InitializationHeuristics FromHardSearchEdges (
		final double searchStartLeft,
		final double searchStartRight)
	{
		try {
			return new InitializationHeuristics (
				SEARCH_HARD_BRACKETS,
				searchStartLeft,
				searchStartRight,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				null
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from the bracketing edge soft hints
	 * 
	 * @param startingBracketLeft Starting Soft Left Bracketing Edge Hint
	 * @param startingBracketRight Starting Soft Right Bracketing Edge Hint
	 * 
	 * @return <i>InitializationHeuristics</i> instance
	 */

	public static final InitializationHeuristics FromBracketingEdgeHints (
		final double startingBracketLeft,
		final double startingBracketRight)
	{
		try {
			return new InitializationHeuristics (
				BRACKETING_EDGE_HINTS,
				Double.NaN,
				Double.NaN,
				startingBracketLeft,
				startingBracketRight,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				null
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from the bracketing mid hint
	 * 
	 * @param startingBracketMid Starting Soft Right Bracketing Mid Hint
	 * 
	 * @return <i>InitializationHeuristics</i> instance
	 */

	public static final InitializationHeuristics FromBracketingMidHint (
		final double startingBracketMid)
	{
		try {
			return new InitializationHeuristics (
				BRACKETING_MID_HINT,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				startingBracketMid,
				Double.NaN,
				Double.NaN,
				null
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from the bracketing hard floor/ceiling
	 * 
	 * @param bracketFloor Starting Hard Left Bracketing Floor
	 * @param bracketCeiling Starting Hard Right Bracketing Ceiling
	 * 
	 * @return <i>InitializationHeuristics</i> instance
	 */

	public static final InitializationHeuristics FromBracketingFloorCeiling (
		final double bracketFloor,
		final double bracketCeiling)
	{
		try {
			return new InitializationHeuristics (
				BRACKETING_FLOOR_CEILING,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				bracketFloor,
				bracketCeiling,
				null
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from Custom Bracketing Control Parameters
	 * 
	 * @param customBracketingControlParams Custom Bracketing Control Parameters
	 * 
	 * @return <i>InitializationHeuristics</i> instance
	 */

	public static final InitializationHeuristics FromBracketingCustomBCP (
		final BracketingControlParams customBracketingControlParams)
	{
		try {
			return new InitializationHeuristics (
				BRACKETING_CUSTOM_BCP,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				Double.NaN,
				customBracketingControlParams
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Construct an <i>InitializationHeuristics</i> Instance from the set of Heuristics Parameters
	 * 
	 * @param determinant Initialization Heuristics Instance Type
	 * @param searchStartLeft Hard Search Start Left Edge
	 * @param searchStartRight Hard Search Start Right Edge
	 * @param startingBracketLeft Starting Soft Left Bracketing Edge Hint
	 * @param startingBracketRight Starting Soft Right Bracketing Edge Hint
	 * @param startingBracketMid Starting Soft Right Bracketing Mid Hint
	 * @param bracketFloor Starting Hard Left Bracketing Floor
	 * @param bracketCeiling Starting Hard Right Bracketing Ceiling
	 * @param customBracketingControlParams Custom Bracketing Control Parameters
	 * 
	 * @throws Exception Thrown if the Input Determinant/parameters are unknown/invalid
	 */

	public InitializationHeuristics (
		final int determinant,
		final double searchStartLeft,
		final double searchStartRight,
		final double startingBracketLeft,
		final double startingBracketRight,
		final double startingBracketMid,
		final double bracketFloor,
		final double bracketCeiling,
		final BracketingControlParams customBracketingControlParams)
		throws Exception
	{
		if (BRACKETING_EDGE_HINTS == (_determinant = determinant)) {
			if (!NumberUtil.IsValid (_startingBracketLeft = startingBracketLeft) ||
				!NumberUtil.IsValid (_startingBracketRight = startingBracketRight))
			{
				throw new Exception (
					"InitializationHeuristics constructor: Invalid BRACKETING_EDGE_HINTS params!"
				);
			}
		} else if (BRACKETING_MID_HINT == _determinant) {
			if (!NumberUtil.IsValid (_startingBracketMid = startingBracketMid)) {
				throw new Exception (
					"InitializationHeuristics constructor: Invalid BRACKETING_MID_HINT params!"
				);
			}
		} else if (BRACKETING_FLOOR_CEILING == _determinant) {
			if (!NumberUtil.IsValid (_bracketFloor = bracketFloor) ||
				!NumberUtil.IsValid (_bracketCeiling = bracketCeiling))
			{
				throw new Exception (
					"InitializationHeuristics constructor: Invalid BRACKETING_FLOOR_CEILING params!"
				);
			}
		} else if (SEARCH_HARD_BRACKETS == _determinant) {
			if (!NumberUtil.IsValid (_searchStartLeft = searchStartLeft) ||
				!NumberUtil.IsValid (_searchStartRight = searchStartRight))
			{
				throw new Exception (
					"InitializationHeuristics constructor: Invalid SEARCH_HARD_BRACKETS params!"
				);
			}
		} else if (BRACKETING_CUSTOM_BCP == _determinant) {
			if (null == (_customBracketingControlParams = customBracketingControlParams)) {
				throw new Exception (
					"InitializationHeuristics constructor: Invalid BRACKETING_CUSTOM_BCP params!"
				);
			}
		} else if (BRACKETING_GENERIC_BCP != _determinant) {
			throw new Exception (
				"InitializationHeuristics constructor: Invalid BRACKETING_GENERIC_BCP params!"
			);
		}
	}

	/**
	 * Retrieve the Determinant
	 * 
	 * @return The Determinant
	 */

	public int determinant()
	{
		return _determinant;
	}

	/**
	 * Retrieve the Hard Left Search Start
	 * 
	 * @return The Hard Left Search Start
	 */

	public double searchStartLeft()
	{
		return _searchStartLeft;
	}

	/**
	 * Retrieve the Hard Right Search Start
	 * 
	 * @return The Hard Right Search Start
	 */

	public double searchStartRight()
	{
		return _searchStartRight;
	}

	/**
	 * Retrieve the Soft Bracket Start Mid
	 * 
	 * @return The Soft Bracket Start Mid
	 */

	public double startingBracketMid()
	{
		return _startingBracketMid;
	}

	/**
	 * Retrieve the Soft Bracket Start Left
	 * 
	 * @return The Soft Bracket Start Left
	 */

	public double startingBracketLeft()
	{
		return _startingBracketLeft;
	}

	/**
	 * Retrieve the Hard Bracket Floor
	 * 
	 * @return The Hard Bracket Floor
	 */

	public double bracketFloor()
	{
		return _bracketFloor;
	}

	/**
	 * Retrieve the Soft Bracket Start Right
	 * 
	 * @return The Soft Bracket Start Right
	 */

	public double startingBracketRight()
	{
		return _startingBracketRight;
	}

	/**
	 * Retrieve the Hard Bracket Ceiling
	 * 
	 * @return The Hard Bracket Ceiling
	 */

	public double bracketCeiling()
	{
		return _bracketCeiling;
	}

	/**
	 * Retrieve the Custom <i>BracketingControlParams</i>
	 * 
	 * @return The Custom <i>BracketingControlParams</i>
	 */

	public BracketingControlParams customBracketingControlParams()
	{
		return _customBracketingControlParams;
	}
}
