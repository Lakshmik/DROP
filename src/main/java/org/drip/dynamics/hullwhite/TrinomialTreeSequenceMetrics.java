
package org.drip.dynamics.hullwhite;

import java.util.HashMap;
import java.util.Map;

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
 * <i>TrinomialTreeSequenceMetrics</i> records the Evolution Metrics of the Hull-White Model Trinomial Tree
 * 	Sequence. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Empty <i>TrinomialTreeSequenceMetrics</i> Constructor</li>
 * 		<li>Add a Path Transition Metrics Instance</li>
 * 		<li>Retrieve the Transition Metrics associated with the specified Tree Time Index</li>
 * 		<li>Retrieve the Transition Metrics Map</li>
 * 		<li>Add the Hull-White Node Metrics Instance</li>
 * 		<li>Retrieve the Node Metrics from the corresponding Tree Time/Space Indexes</li>
 * 		<li>Retrieve the Node Metrics Map</li>
 * 		<li>Set the Transition Probability for the specified Pair of Nodes</li>
 * 		<li>Retrieve the Source-To-Target Transition Probability</li>
 * 		<li>Retrieve the FULL Source-Target Transition Probability Map</li>
 * 		<li>Retrieve the Target-From-Source Transition Probability</li>
 * 		<li>Retrieve the FULL Target-Source Transition Probability Map</li>
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

public class TrinomialTreeSequenceMetrics
{
	private Map<String, Double> _sourceTargetTransitionProbabilityMap = new HashMap<String, Double>();

	private Map<String, Double> _targetSourceTransitionProbabilityMap = new HashMap<String, Double>();

	private Map<Long, TrinomialTreeTransitionMetrics> _trinomialTreeTransitionMetricsMap =
		new HashMap<Long, TrinomialTreeTransitionMetrics>();

	private Map<String, TrinomialTreeNodeMetrics> _trinomialTreeNodeMetricsMap =
		new HashMap<String, TrinomialTreeNodeMetrics>();

	private static final String NodeMetricsKey (
		final TrinomialTreeNodeMetrics trinomialTreeTransitionMetrics)
	{
		return trinomialTreeTransitionMetrics.timeIndex() + "," +
			trinomialTreeTransitionMetrics.xStochasticIndex();
	}

	/**
	 * Empty <i>TrinomialTreeSequenceMetrics</i> Constructor
	 */

	public TrinomialTreeSequenceMetrics()
	{
	}

	/**
	 * Add a Path Transition Metrics Instance
	 * 
	 * @param trinomialTreeTransitionMetrics The Path Transition Metrics Instance
	 * 
	 * @return TRUE - The Path Transition Metrics Instance successfully added
	 */

	public boolean addTransitionMetrics (
		final TrinomialTreeTransitionMetrics trinomialTreeTransitionMetrics)
	{
		if (null == trinomialTreeTransitionMetrics) {
			return false;
		}

		_trinomialTreeTransitionMetricsMap.put (
			trinomialTreeTransitionMetrics.treeTimeIndex(),
			trinomialTreeTransitionMetrics
		);

		return true;
	}

	/**
	 * Retrieve the Transition Metrics associated with the specified Tree Time Index
	 * 
	 * @param treeTimeIndex The Tree Time Index
	 * 
	 * @return The Transition Metrics associated with the specified Tree Time Index
	 */

	public TrinomialTreeTransitionMetrics transitionMetrics (
		final long treeTimeIndex)
	{
		return _trinomialTreeTransitionMetricsMap.containsKey (treeTimeIndex) ?
			_trinomialTreeTransitionMetricsMap.get (treeTimeIndex) : null;
	}

	/**
	 * Retrieve the Transition Metrics Map
	 * 
	 * @return The Transition Metrics Map
	 */

	public Map<Long, TrinomialTreeTransitionMetrics> trinomialTreeTransitionMetricsMap()
	{
		return _trinomialTreeTransitionMetricsMap;
	}

	/**
	 * Add the Hull-White Node Metrics Instance
	 * 
	 * @param trinomialTreeTransitionMetrics The Hull-White Node Metrics Instance
	 * 
	 * @return The Node Met5rics Instance successfully added
	 */

	public boolean addNodeMetrics (
		final TrinomialTreeNodeMetrics trinomialTreeTransitionMetrics)
	{
		if (null == trinomialTreeTransitionMetrics) {
			return false;
		}

		_trinomialTreeNodeMetricsMap.put (
			NodeMetricsKey (trinomialTreeTransitionMetrics),
			trinomialTreeTransitionMetrics
		);

		return true;
	}

	/**
	 * Retrieve the Node Metrics from the corresponding Tree Time/Space Indexes
	 * 
	 * @param treeTimeIndex The Tree Time Index
	 * @param treeStochasticIndex The Tree Space Index
	 * 
	 * @return The Node Metrics
	 */

	public TrinomialTreeNodeMetrics nodeMetrics (
		final long treeTimeIndex,
		final long treeStochasticIndex)
	{
		String key = treeTimeIndex + "," + treeStochasticIndex;

		return _trinomialTreeNodeMetricsMap.containsKey (key) ?
			_trinomialTreeNodeMetricsMap.get (key) : null;
	}

	/**
	 * Retrieve the Node Metrics Map
	 * 
	 * @return The Node Metrics Map
	 */

	public Map<String, TrinomialTreeNodeMetrics> trinomialTreeNodeMetricsMap()
	{
		return _trinomialTreeNodeMetricsMap;
	}

	/**
	 * Set the Transition Probability for the specified Pair of Nodes
	 * 
	 * @param sourceTrinomialTreeNodeMetrics Source Node
	 * @param targetTrinomialTreeNodeMetrics Target Node
	 * @param transitionProbability The Transition Probability
	 * 
	 * @return TRUE - The Transition Probability Successfully set
	 */

	public boolean setTransitionProbability (
		final TrinomialTreeNodeMetrics sourceTrinomialTreeNodeMetrics,
		final TrinomialTreeNodeMetrics targetTrinomialTreeNodeMetrics,
		final double transitionProbability)
	{
		if (null == sourceTrinomialTreeNodeMetrics ||
			null == targetTrinomialTreeNodeMetrics ||
			!NumberUtil.IsValid (transitionProbability) ||
				0. >= transitionProbability || 1. < transitionProbability)
		{
			return false;
		}

		String sourceNodeKey = NodeMetricsKey (sourceTrinomialTreeNodeMetrics);

		String targetNodeKey = NodeMetricsKey (targetTrinomialTreeNodeMetrics);

		_sourceTargetTransitionProbabilityMap.put (
			sourceNodeKey + "#" + targetNodeKey,
			transitionProbability
		);

		_targetSourceTransitionProbabilityMap.put (
			targetNodeKey + "#" + sourceNodeKey,
			transitionProbability
		);

		return true;
	}

	/**
	 * Retrieve the Source-To-Target Transition Probability
	 * 
	 * @param sourceTrinomialTreeNodeMetrics Source Node
	 * @param targetTrinomialTreeNodeMetrics Target Node
	 * 
	 * @return The Source-To-Target Transition Probability
	 * 
	 * @throws Exception Thrown if the Source-To-Target Transition Probability cannot be computed
	 */

	public double sourceTargetTransitionProbability (
		final TrinomialTreeNodeMetrics sourceTrinomialTreeNodeMetrics,
		final TrinomialTreeNodeMetrics targetTrinomialTreeNodeMetrics)
		throws Exception
	{
		if (null == sourceTrinomialTreeNodeMetrics || null == targetTrinomialTreeNodeMetrics) {
			throw new Exception (
				"TrinomialTreeSequenceMetrics::sourceTargetTransitionProbability => Invalid Inputs!"
			);
		}

		String key = NodeMetricsKey (sourceTrinomialTreeNodeMetrics) + "#" +
			NodeMetricsKey (targetTrinomialTreeNodeMetrics);

		if (!_sourceTargetTransitionProbabilityMap.containsKey (key)) {
			throw new Exception (
				"TrinomialTreeSequenceMetrics::sourceTargetTransitionProbability => No Transition Entry!"
			);
		}

		return _sourceTargetTransitionProbabilityMap.get (key);
	}

	/**
	 * Retrieve the FULL Source-Target Transition Probability Map
	 * 
	 * @return The Source-Target Transition Probability Map
	 */

	public Map<String, Double> sourceTargetTransitionProbability()
	{
		return _sourceTargetTransitionProbabilityMap;
	}

	/**
	 * Retrieve the Target-From-Source Transition Probability
	 * 
	 * @param targetTrinomialTreeNodeMetrics Target Node
	 * @param sourceTargetTransitionProbability Source Node
	 * 
	 * @return The Target-From-Source Transition Probability
	 * 
	 * @throws Exception Thrown if the Target-From-Source Transition Probability cannot be computed
	 */

	public double targetSourceTransitionProbability (
		final TrinomialTreeNodeMetrics targetTrinomialTreeNodeMetrics,
		final TrinomialTreeNodeMetrics sourceTargetTransitionProbability)
		throws Exception
	{
		if (null == sourceTargetTransitionProbability || null == targetTrinomialTreeNodeMetrics) {
			throw new Exception (
				"TrinomialTreeSequenceMetrics::targetSourceTransitionProbability => Invalid Inputs!"
			);
		}

		String key = NodeMetricsKey (targetTrinomialTreeNodeMetrics) + "#" +
			NodeMetricsKey (sourceTargetTransitionProbability);

		if (!_targetSourceTransitionProbabilityMap.containsKey (key)) {
			throw new Exception (
				"TrinomialTreeSequenceMetrics::targetSourceTransitionProbability => No Transition Entry!"
			);
		}

		return _targetSourceTransitionProbabilityMap.get (key);
	}

	/**
	 * Retrieve the FULL Target-Source Transition Probability Map
	 * 
	 * @return The Target-Source Transition Probability Map
	 */

	public Map<String, Double> targetSourceTransitionProbability()
	{
		return _targetSourceTransitionProbabilityMap;
	}
}
