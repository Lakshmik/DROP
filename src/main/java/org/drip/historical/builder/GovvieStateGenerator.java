
package org.drip.historical.builder;

import java.util.Map;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.support.CaseInsensitiveHashMap;
import org.drip.historical.calibration.GovvieMarkSuite;
import org.drip.historical.calibration.GovvieInstrumentSuite;
import org.drip.product.govvie.TreasuryComponent;
import org.drip.service.template.TreasuryBuilder;
import org.drip.state.creator.ScenarioGovvieCurveBuilder;
import org.drip.state.govvie.GovvieCurve;

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
 * <i>GovvieStateGenerator</i> generates the T1/T2 Straight Up/Rolled Govvie States. It provides the
 * 	following Functionality:
 *
 *  <ul>
 * 		<li><i>GovvieStateGenerator</i> Constructor</li>
 * 		<li>Retrieve the Govvie Customization Settings</li>
 * 		<li>Construct the Array of Treasury Components</li>
 * 		<li>Generate the T0 EOD Govvie State Evaluation Results</li>
 * 		<li>Generate the T1 SOD Evaluated Govvie State</li>
 * 		<li>Generate the Map of T0 EOD and the T1 SOD Govvie States</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/builder/README.md">Latent State T1/T2 Builders</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class GovvieStateGenerator
{
	private GovvieStateCustomization _customizationSettings = null;

	/**
	 * <i>GovvieStateGenerator</i> Constructor
	 * 
	 * @param customizationSettings Customization Settings
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public GovvieStateGenerator (
		final GovvieStateCustomization customizationSettings)
		throws Exception
	{
		if (null == (_customizationSettings = customizationSettings)) {
			throw new Exception ("GovvieStateGenerator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Govvie Customization Settings
	 * 
	 * @return Govvie Customization Settings
	 */

	public GovvieStateCustomization customizationSettings()
	{
		return _customizationSettings;
	}

	/**
	 * Construct the Array of Treasury Components
	 * 
	 * @param effectiveDate Effective Date
	 * 
	 * @return Array of Treasury Components
	 */

	public TreasuryComponent[] treasuryComponentArray (
		final JulianDate effectiveDate)
	{
		GovvieInstrumentSuite govvieInstrumentSuite = _customizationSettings.instrumentSuite();

		String[] maturityTenorArray = govvieInstrumentSuite.maturityTenorArray();

		JulianDate[] maturityDateArray = new JulianDate[maturityTenorArray.length];
		JulianDate[] effectiveDateArray = new JulianDate[maturityTenorArray.length];

		for (int tenorIndex = 0; tenorIndex < maturityTenorArray.length; ++tenorIndex) {
			maturityDateArray[tenorIndex] = (
				effectiveDateArray[tenorIndex] = effectiveDate
			).addTenor (maturityTenorArray[tenorIndex]);
		}

		return TreasuryBuilder.FromCode (
			govvieInstrumentSuite.code(),
			effectiveDateArray,
			maturityDateArray,
			govvieInstrumentSuite.couponArray()
		);
	}

	/**
	 * Generate the T0 EOD Govvie State Evaluation Results
	 * 
	 * @param govvieMarkSuite Govvie Mark Quote Suite
	 * 
	 * @return T0 EOD Govvie State Evaluation Results
	 */

	public GovvieStateEvaluation t0EOD (
		final GovvieMarkSuite govvieMarkSuite)
	{
		if (null == govvieMarkSuite) {
			return null;
		}

		GovvieInstrumentSuite govvieSuite = _customizationSettings.instrumentSuite();

		JulianDate t0 = govvieMarkSuite.asOfDate();

		String treasuryCode = govvieSuite.code();

		TreasuryComponent[] treasuryComponentArray = treasuryComponentArray (t0);

		try {
			return new GovvieStateEvaluation (
				ScenarioGovvieCurveBuilder.CubicPolyShapePreserver (
					treasuryCode,
					treasuryCode,
					govvieSuite.currency(),
					t0.julian(),
					treasuryComponentArray,
					govvieMarkSuite.treasuryQuoteArray(),
					govvieSuite.calibrationMetric()
				),
				govvieMarkSuite,
				treasuryComponentArray
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the T1 SOD Evaluated Govvie State
	 * 
	 * @param t1 T1
	 * @param t0GovvieCurve T0 Govvie Curve
	 * 
	 * @return T1 SOD Evaluated Govvie State
	 */

	public GovvieStateEvaluation t1SOD (
		final JulianDate t1,
		final GovvieCurve t0GovvieCurve)
	{
		TreasuryComponent[] t1TreasuryComponentArray = treasuryComponentArray (t1);

		double[] t1TreasuryQuoteArray = new double[t1TreasuryComponentArray.length];

		GovvieInstrumentSuite govvieSuite = _customizationSettings.instrumentSuite();

		String treasuryCode = govvieSuite.code();

		for (int treasuryComponentIndex = 0;
			treasuryComponentIndex < t1TreasuryComponentArray.length;
			++treasuryComponentIndex)
		{
			try {
				t1TreasuryQuoteArray[treasuryComponentIndex] =
					t0GovvieCurve.yld (t1TreasuryComponentArray[treasuryComponentIndex].maturityDate());
			} catch (Exception e) {
				e.printStackTrace();

				return null;
			}
		}

		try {
			return new GovvieStateEvaluation (
				ScenarioGovvieCurveBuilder.CubicPolyShapePreserver (
					treasuryCode,
					treasuryCode,
					govvieSuite.currency(),
					t1.julian(),
					t1TreasuryComponentArray,
					t1TreasuryQuoteArray,
					govvieSuite.calibrationMetric()
				),
				new GovvieMarkSuite (t1, t1TreasuryQuoteArray),
				t1TreasuryComponentArray
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Generate the Map of T0 EOD and the T1 SOD Govvie States
	 * 
	 * @param t0GovvieMarkSuite T0 Govvie Mark Suite
	 * @param t1 T1
	 * 
	 * @return Map of T0 EOD and the T1 SOD Govvie States
	 */

	public Map<String, GovvieStateEvaluation> t0EODT1SOD (
		final GovvieMarkSuite t0GovvieMarkSuite,
		final JulianDate t1)
	{
		GovvieStateEvaluation t0EODGovvieStateEvaluation = t0EOD (t0GovvieMarkSuite);

		if (null == t0EODGovvieStateEvaluation) {
			return null;
		}

		Map<String, GovvieStateEvaluation> govvieStateEvaluationMap =
			new CaseInsensitiveHashMap<GovvieStateEvaluation>();

		govvieStateEvaluationMap.put ("TOEOD", t0EODGovvieStateEvaluation);

		govvieStateEvaluationMap.put ("T1SOD", t1SOD (t1, t0EODGovvieStateEvaluation.govvieCurve()));

		return govvieStateEvaluationMap;
	}
}
