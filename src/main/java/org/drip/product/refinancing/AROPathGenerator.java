
package org.drip.product.refinancing;

import java.util.TreeMap;

import org.drip.analytics.date.JulianDate;
import org.drip.analytics.daycount.Convention;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.credit.BondComponent;
import org.drip.product.muni.DeGuillaumeRebonatoPogudinPath;
import org.drip.product.muni.EOSBasis;
import org.drip.product.muni.IssueCurveMeasures;
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
 * <i>AROPathGenerator</i> generates the Single Path incurred by the ARO Process using the Muni Sub-markets,
 * 	i.e., Tax-exempt, and Taxable Path Yield Curves. The References are:
 *
 *  <br><br>
 *  <ul>
 *  	<li>
 *  		Ang, A., R. C. Green, and Y. Xing (2013): <i>Advance Re-fundings of Municipal Bonds</i>
 *  			https://www.nber.org/papers/w19459
 *  	</li>
 *  	<li>
 *  		de Guillaume, N., R. Rebonato, and A. Pogudin (2013): The Nature of the Dependence of the
 *  			Magnitude of the Rates Moves on the Rates Levels: A Universal Relationship <i>Quantitative
 *  			Finance</i> <b>13 (3)</b> 351-367
 *  	</li>
 *  	<li>
 *  		Orr, P., and D. de la Nuez (2013): <i>The Right and Wrong Models for Evaluating Callable
 *  			Municipal Bonds</i> <b>eSSRN</b>
 *  	</li>
 *  	<li>
 *  		Rebonato, R. (2003): <i>Term Structure Models: A Review</i>
 *  			https://dept.math.lsa.umich.edu/~conlon/math623/rebonato_review.pdf
 *  	</li>
 *  	<li>
 *  		Rebonato, R., and S. K. Nawalkha (2011): <i>What Interest Rate Models to Use? Buy Side versus
 *  			Sell Side</i> <b>eSSRN</b>
 *  	</li>
 *  </ul>
 *
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/README.md">Product Components/Baskets for Credit, FRA, FX, Govvie, Rates, and Option Asset Classes</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/product/refinancing/README.md">Evaluation of Product Re-financing PnL</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class AROPathGenerator
{
	private AROSetting _setting = null;
	private BondComponent _issueBond = null;

	/**
	 * <i>AROPathGenerator</i> Constructor
	 * 
	 * @param issueBond Issue Bond
	 * @param setting <i>AROSetting</i> Instance
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public AROPathGenerator (
		final BondComponent issueBond,
		final AROSetting setting)
		throws Exception
	{
		if (null == (_issueBond = issueBond) || null == (_setting = setting)) {
			throw new Exception ("AROPathGenerator Constructor => Invalid Inputs");
		}
	}

	/**
	 * Retrieve the Issue Bond
	 * 
	 * @return Issue Bond
	 */

	public BondComponent issueBond()
	{
		return _issueBond;
	}

	/**
	 * Retrieve the <i>AROSetting</i> Instance
	 * 
	 * @return <i>AROSetting</i> Instance
	 */

	public AROSetting setting()
	{
		return _setting;
	}

	/**
	 * Generate <i>AROPathEntry</i> Instance
	 * 
	 * @param deGuillaumeRebonatoPogudinPath Simulated <i>DeGuillaumeRebonatoPogudinPath</i> Instance
	 * @param eosBasis <i>EOSBasis</i> Instance
	 * @param taxExempt TRUE - Apply Tax-exempt Re-financing
	 * 
	 * @return <i>AROPathEntry</i> Instance
	 */

	public AROPathEntry generate (
		final DeGuillaumeRebonatoPogudinPath deGuillaumeRebonatoPogudinPath,
		final EOSBasis eosBasis,
		final boolean taxExempt)
	{
		if (null == deGuillaumeRebonatoPogudinPath) {
			return null;
		}

		BondComponent escrowBond = _setting.escrowBond();

		JulianDate refinancingDate = _setting.refinancingDate();

		int refinancingDateJulian = refinancingDate.julian();

		JulianDate embeddedOptionExerciseDate = _setting.embeddedOptionExerciseDate();

		int embeddedOptionExerciseJulian = embeddedOptionExerciseDate.julian();

		ValuationParams refinancingDateValuationParameters = ValuationParams.Spot (refinancingDateJulian);

		ValuationParams embeddedOptionExerciseDateValuationParameters =
			ValuationParams.Spot (embeddedOptionExerciseDate.julian());

		TreeMap<JulianDate, GovvieCurve> dateEscrowGovvieCurveMap =
			deGuillaumeRebonatoPogudinPath.dateEscrowGovvieCurveMap();

		TreeMap<JulianDate, GovvieCurve> dateIssuerGovvieCurveMap = taxExempt ?
			deGuillaumeRebonatoPogudinPath.dateTaxableGovvieCurveMap() :
			deGuillaumeRebonatoPogudinPath.dateTaxExemptGovvieCurveMap();

		try {
			return new AROPathEntry (
				IssueCurveMeasures.PriceFromGovvie (
					_issueBond,
					refinancingDateValuationParameters,
					dateIssuerGovvieCurveMap.get (refinancingDate),
					eosBasis
				),
				_setting.embeddedOptionExercisePrice(),
				_issueBond.couponMetrics (
					embeddedOptionExerciseJulian,
					refinancingDateValuationParameters,
					null
				).rate() * Convention.YearFraction (
					refinancingDateJulian,
					embeddedOptionExerciseJulian,
					_issueBond.couponDC(),
					false,
					null,
					_issueBond.currency()
				),
				IssueCurveMeasures.PriceFromGovvie (
					escrowBond,
					refinancingDateValuationParameters,
					dateEscrowGovvieCurveMap.get (refinancingDate),
					null
				),
				IssueCurveMeasures.PriceFromGovvie (
					escrowBond,
					embeddedOptionExerciseDateValuationParameters,
					dateEscrowGovvieCurveMap.get (embeddedOptionExerciseDate),
					null
				),
				escrowBond.couponMetrics (
					embeddedOptionExerciseJulian,
					refinancingDateValuationParameters,
					null
				).rate() * Convention.YearFraction (
					refinancingDateJulian,
					embeddedOptionExerciseJulian,
					escrowBond.couponDC(),
					false,
					null,
					escrowBond.currency()
				),
				_setting.refinancingCharge()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
