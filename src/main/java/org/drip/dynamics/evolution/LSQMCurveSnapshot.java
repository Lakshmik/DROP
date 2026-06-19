
package org.drip.dynamics.evolution;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drip.analytics.definition.Curve;
import org.drip.state.identifier.LatentStateLabel;

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
 * <i>LSQMCurveSnapshot</i> contains the Snapshot of the Evolving Term Structure of the Latent State
 * 	Quantification Metrics. It provides the following Functions:
 *
 *  <ul>
 * 		<li>Empty <i>LSQMCurveSnapshot</i> Constructor</li>
 * 		<li>Retrieve the Latent State Labels</li>
 * 		<li>Indicate if Quantification Metrics are available for the specified Latent State</li>
 * 		<li>Indicate if the Value for the specified Quantification Metric is available</li>
 * 		<li>Set the Latent State Quantification Metric Curve</li>
 * 		<li>Retrieve the specified Latent State Quantification Metric Curve</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/README.md">HJM, Hull White, LMM, and SABR Dynamic Evolution Models</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/dynamics/evolution/README.md">Latent State Evolution Edges/Vertexes</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class LSQMCurveSnapshot
{
	private Map<String, Map<String, Curve>> _labelQuantificationMetricCurveMap =
		new HashMap<String, Map<String, Curve>>();

	/**
	 * Empty <i>LSQMCurveSnapshot</i> Constructor
	 * Retrieve the Latent State Labels
	 * Indicate if Quantification Metrics are available for the specified Latent State
	 * Indicate if the Value for the specified Quantification Metric is available
	 * Set the Latent State Quantification Metric Curve
	 * Retrieve the specified Latent State Quantification Metric Curve
	 */

	public LSQMCurveSnapshot()
	{
	}

	/**
	 * Retrieve the Latent State Labels
	 * 
	 * @return The Latent State Labels
	 */

	public Set<String> latentStateLabelSet()
	{
		return _labelQuantificationMetricCurveMap.keySet();
	}

	/**
	 * Indicate if Quantification Metrics are available for the specified Latent State
	 * 
	 * @param latentStateLabel The Latent State Label
	 * 
	 * @return TRUE - Quantification Metrics are available for the specified Latent State
	 */

	public boolean containsLatentState (
		final LatentStateLabel latentStateLabel)
	{
		return null != latentStateLabel &&
			_labelQuantificationMetricCurveMap.containsKey (latentStateLabel.fullyQualifiedName());
	}

	/**
	 * Indicate if the Value for the specified Quantification Metric is available
	 * 
	 * @param latentStateLabel The Latent State Label
	 * @param quantificationMetric The Quantification Metric
	 * 
	 * @return TRUE - The Requested Value is available
	 */

	public boolean containsQuantificationMetric (
		final LatentStateLabel latentStateLabel,
		final String quantificationMetric)
	{
		if (null == latentStateLabel || null == quantificationMetric || quantificationMetric.isEmpty()) {
			return false;
		}

		String label = latentStateLabel.fullyQualifiedName();

		return _labelQuantificationMetricCurveMap.containsKey (label) &&
			_labelQuantificationMetricCurveMap.get (label).containsKey (quantificationMetric);
	}

	/**
	 * Set the LSQM Curve
	 * Retrieve the specified Latent State Quantification Metric Curve
	 * 
	 * @param quantificationMetric The Quantification Metric
	 * @param quantificationMetricCurve The Quantification Metric Curve
	 * 
	 * @return TRUE - The Quantification Metric successfully set
	 */

	public boolean setQuantificationMetricCurve (
		final String quantificationMetric,
		final Curve quantificationMetricCurve)
	{
		if (null == quantificationMetric || quantificationMetric.isEmpty() ||
			null == quantificationMetricCurve) {
			return false;
		}

		String label = quantificationMetricCurve.label().fullyQualifiedName();

		Map<String, Curve> quantificationMetricCurveMap =
			_labelQuantificationMetricCurveMap.containsKey (label) ?
			_labelQuantificationMetricCurveMap.get (label) : new HashMap<String, Curve>();

		quantificationMetricCurveMap.put (quantificationMetric, quantificationMetricCurve);

		_labelQuantificationMetricCurveMap.put (label, quantificationMetricCurveMap);

		return true;
	}

	/**
	 * Retrieve the specified Latent State Quantification Metric Curve
	 * 
	 * @param latentStateLabel The Latent State Label
	 * @param quantificationMetric The Quantification Metric
	 * 
	 * @return The Latent State Quantification Metric Curve
	 */

	public Curve quantificationMetricCurve (
		final LatentStateLabel latentStateLabel,
		final String quantificationMetric)
	{
		if (null == latentStateLabel || null == quantificationMetric || quantificationMetric.isEmpty()) {
			return null;
		}

		Map<String, Curve> quantificationMetricCurveMap = _labelQuantificationMetricCurveMap.get
			(latentStateLabel.fullyQualifiedName());

		return quantificationMetricCurveMap.containsKey (quantificationMetric) ?
			quantificationMetricCurveMap.get (quantificationMetric) : null;
	}
}
