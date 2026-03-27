
package org.drip.historical.sensitivity;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.analytics.support.CaseInsensitiveTreeMap;
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
 * <i>TenorDurationNodeMetrics</i> holds the KRD Duration Nodes and associated Metrics. It provides the
 * 	following Functionality:
 *
 *  <ul>
 * 		<li><i>TenorDurationNodeMetrics</i> Constructor</li>
 * 		<li>Retrieve the KRD Date Snap</li>
 * 		<li>Insert a KRD Node</li>
 * 		<li>Retrieve the KRD Map</li>
 * 		<li>Set the Custom Date Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom Date Entry corresponding to the Specified Key</li>
 * 		<li>Set the Custom C<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom C<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Set the Custom R<sup>1</sup> Entry corresponding to the Specified Key</li>
 * 		<li>Retrieve the Custom R<sup>1</sup> Entry corresponding to the Specified Key</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/sensitivity/README.md">Product Horizon Change Tenor Sensitivity</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class TenorDurationNodeMetrics
{
	private JulianDate _snapDate = null;

	private Map<String, String> _customC1Map = new CaseInsensitiveHashMap<String>();

	private Map<String, Double> _customR1Map = new CaseInsensitiveHashMap<Double>();

	private CaseInsensitiveTreeMap<Double> _krdMap = new CaseInsensitiveTreeMap<Double>();

	private Map<String, JulianDate> _customDateMap = new CaseInsensitiveHashMap<JulianDate>();

	/**
	 * <i>TenorDurationNodeMetrics</i> Constructor
	 * 
	 * @param snapDate The Date Snap
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public TenorDurationNodeMetrics (
		final JulianDate snapDate)
		throws Exception
	{
		if (null == (_snapDate = snapDate)) {
			throw new Exception ("TenorDurationNodeMetrics Constructor: Invalid Inputs");
		}
	}

	/**
	 * Retrieve the KRD Date Snap
	 * 
	 * @return The KRD Date Snap
	 */

	public JulianDate snapDate()
	{
		return _snapDate;
	}

	/**
	 * Insert a KRD Node
	 * 
	 * @param krdKey KRD Node Key
	 * @param krdValue KRD Node Value
	 * 
	 * @return TRUE - The KRD Entry successfully inserted
	 */

	public boolean addKRDNode (
		final String krdKey,
		final double krdValue)
	{
		if (null == krdKey || krdKey.isEmpty() || !NumberUtil.IsValid (krdValue)) {
			return false;
		}

		_krdMap.put (krdKey, krdValue);

		return true;
	}

	/**
	 * Retrieve the KRD Map
	 * 
	 * @return The KRD Map
	 */

	public CaseInsensitiveTreeMap<Double> krdMap()
	{
		return _krdMap;
	}

	/**
	 * Set the Custom Date Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param customDate The Custom Date Entry
	 * 
	 * @return TRUE - Custom Date successfully set
	 */

	public boolean setDate (
		final String key,
		final JulianDate customDate)
	{
		if (null == key || key.isEmpty() || null == customDate) {
			return false;
		}

		_customDateMap.put (key, customDate);

		return true;
	}

	/**
	 * Retrieve the Custom Date Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom Date Entry
	 */

	public JulianDate date (
		final String key)
	{
		return null == key || !_customDateMap.containsKey (key) ? null : _customDateMap.get (key);
	}

	/**
	 * Set the Custom C<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param c1 The Custom C<sup>1</sup> Entry
	 * 
	 * @return TRUE - Custom C<sup>1</sup> Entry successfully set
	 */

	public boolean setC1 (
		final String key,
		final String c1)
	{
		if (null == key || key.isEmpty() || null == c1 || c1.isEmpty()) {
			return false;
		}

		_customC1Map.put (key, c1);

		return true;
	}

	/**
	 * Retrieve the Custom C<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom C<sup>1</sup> Entry
	 */

	public String c1 (
		final String key)
	{
		return null == key || !_customC1Map.containsKey (key) ? null : _customC1Map.get (key);
	}

	/**
	 * Set the Custom R<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * @param r1 The Custom R<sup>1</sup> Entry
	 * 
	 * @return TRUE - Custom Number successfully set
	 */

	public boolean setR1 (
		final String key,
		final double r1)
	{
		if (null == key || key.isEmpty() || !NumberUtil.IsValid (r1)) {
			return false;
		}

		_customR1Map.put (key, r1);

		return true;
	}

	/**
	 * Retrieve the Custom R<sup>1</sup> Entry corresponding to the Specified Key
	 * 
	 * @param key The Key
	 * 
	 * @return The Custom R<sup>1</sup> Entry
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public double r1 (
		final String key)
		throws Exception
	{
		if (null == key || !_customR1Map.containsKey (key)) {
			throw new Exception ("TenorDurationNodeMetrics::r1 => Invalid Inputs");
		}

		return _customR1Map.get (key);
	}
}
