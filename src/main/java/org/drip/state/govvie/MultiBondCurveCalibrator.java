
package org.drip.state.govvie;

import java.util.List;

import org.drip.analytics.date.JulianDate;
import org.drip.function.definition.RdToR1;
import org.drip.numerical.common.NumberUtil;
import org.drip.param.market.CurveSurfaceQuoteContainer;
import org.drip.param.valuation.ValuationParams;
import org.drip.product.credit.BondComponent;
import org.drip.state.nonlinear.FlatForwardGovvieCurve;

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
 * <i>MultiBondCurveCalibrator</i> calibrates the Govvie Curve from the Set of Bond Quotes using the Nelder
 * 	Mead Scheme. The References are:
 *  
 * 	<br>
 *  <ul>
 * 		<li>
 * 			Black, F., E. Derman, and W. Toy (1990): A One-Factor Model of Interest Rates and Its Application
 * 				to Treasury Bond Options <i>Financial Analysis Journal</i> <b>46 (1)</b> 33-39
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990a): Valuing Derivative Securities Using the Explicit Finite Difference
 * 				Method <i>Journal of Financial and Quantitative Analysis</i> <b>25 (1)</b> 87-100
 * 		</li>
 * 		<li>
 * 			Hull, J. and A. White (1990b): Pricing Interest-Rate-Derivative Securities <i>Review of Financial
 * 				Studies</i> <b>3 (4)</b> 573-592
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J. and G. O. Williams (1992): The Valuation and Management of Bonds with Sinking Fund
 * 				Provisions <i>Financial Analysis Journal</i> <b>48 (2)</b> 59-67
 * 		</li>
 * 		<li>
 * 			Kalotay, A. J., G. O. Williams, and F. J. Fabozzi (1993): A Model for Valuing Bonds and Embedded
 * 				Options <i>Financial Analysis Journal</i> <b>49 (3)</b> 35-46
 * 		</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/ProductCore.md">Product Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/FixedIncomeAnalyticsLibrary.md">Fixed Income Analytics</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmik/DROP/tree/master/src/main/java/org/drip/state/README.md">Latent State Inference and Creation Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/state/govvie/README.md">Govvie Latent State Curve Estimator</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class MultiBondCurveCalibrator
	extends RdToR1
{
	private String _code = "";
	private String _currency = "";
	private JulianDate _epochDate = null;
	private int[] _calibrationDateArray = null;
	private ValuationParams _valuationParams = null;
	private List<MarketComponent> _marketComponentList = null;

	/**
	 * <i>MultiBondCurveCalibrator</i> Constructor
	 * 
	 * @param epochDate Epoch Date
	 * @param code Code
	 * @param currency Currency
	 * @param calibrationDateArray Array of Calibration Dates
	 * @param marketComponentList List of <i>MarketComponent</i> Instances
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public MultiBondCurveCalibrator (
		final JulianDate epochDate,
		final String code,
		final String currency,
		final int[] calibrationDateArray,
		final List<MarketComponent> marketComponentList)
		throws Exception
	{
		super (null);

		if (null == (_epochDate = epochDate) ||
			null == (_code = code) || _code.isEmpty() ||
			null == (_currency = currency) || _currency.isEmpty() ||
			null == (_calibrationDateArray = calibrationDateArray) || 0 == _calibrationDateArray.length ||
			null == (_marketComponentList = marketComponentList) ||
				_marketComponentList.size() <= _calibrationDateArray.length)
		{
			throw new Exception ("MultiBondCurveCalibrator Constructor => Invalid Inputs");
		}

		_valuationParams = ValuationParams.Spot (_epochDate.julian());

		for (MarketComponent marketComponent : marketComponentList) {
			if (null == marketComponent) {
				throw new Exception ("MultiBondCurveCalibrator Constructor => Invalid Inputs");
			}
		}
	}

	/**
	 * Retrieve the Epoch Date
	 * 
	 * @return Epoch Date
	 */

	public JulianDate epochDate()
	{
		return _epochDate;
	}

	/**
	 * Retrieve the Code
	 * 
	 * @return Code
	 */

	public String code()
	{
		return _code;
	}

	/**
	 * Retrieve the Currency
	 * 
	 * @return Currency
	 */

	public String currency()
	{
		return _currency;
	}

	/**
	 * Retrieve the Array of Calibration Dates
	 * 
	 * @return Array of Calibration Dates
	 */

	public int[] calibrationDateArray()
	{
		return _calibrationDateArray;
	}
	/**
	 * Return the List of Calibration Bonds and their Prices
	 * 
	 * @return List of Calibration Bonds and their Prices
	 */

	public List<MarketComponent> marketComponentList()
	{
		return _marketComponentList;
	}

	/**
	 * Retrieve the Dimension of the Input Variate
	 * 
	 * @return The Dimension of the Input Variate
	 */

	@Override public int dimension()
	{
		return _calibrationDateArray.length;
	}

	/**
	 * Evaluate for the given Array of Forward Yields
	 * 
	 * @param forwardYieldArray Array of Forward Yields
	 *  
	 * @return The Calculated Value
	 * 
	 * @throws Exception Thrown if the Evaluation cannot be done
	 */

	public double evaluate (
		final double[] forwardYieldArray)
		throws Exception
	{
		if (null == forwardYieldArray ||
			!NumberUtil.IsValid (forwardYieldArray) ||
			forwardYieldArray.length != _calibrationDateArray.length)
		{
			throw new Exception ("MultiBondCurveCalibrator::evaluate => Invalid Inputs");
		}

		int epochDateJulian = _epochDate.julian();

		CurveSurfaceQuoteContainer curveSurfaceQuoteContainer = new CurveSurfaceQuoteContainer();

		curveSurfaceQuoteContainer.setGovvieState (
			new FlatForwardGovvieCurve (
				epochDateJulian,
				_code,
				_currency,
				_calibrationDateArray,
				forwardYieldArray
			)
		);

		double leastSquaresPriceDifferencesSum = 0.;

		for (MarketComponent marketComponent : _marketComponentList) {
			BondComponent bond = marketComponent.bond();

			double priceDifference = bond.priceFromGSpread (
				_valuationParams,
				curveSurfaceQuoteContainer,
				null,
				0.
			) - bond.accrued (
				epochDateJulian,
				null
			) - marketComponent.cleanPrice();

			leastSquaresPriceDifferencesSum +=
				marketComponent.weight() * (priceDifference * priceDifference);
		}

		return leastSquaresPriceDifferencesSum;
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @param prefix The JSON Prefix
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public String toString (
		final String prefix)
	{
		String dump = prefix + "Multi-Bond Curve Calibrator: {" + _epochDate + ", " + _currency + ", " +
			_code + "} {Pillar Dates: ";

		for (int calibrationDate : _calibrationDateArray) {
			dump += new JulianDate (calibrationDate) + ", ";
		}

		dump += "};\n" + prefix + "Calibration Components:";

		for (MarketComponent marketComponent : _marketComponentList) {
			dump += marketComponent.toString ("\n" + prefix + "\t");
		}

		return dump + "\n";
	}

	/**
	 * 'JSON-ize' the State
	 * 
	 * @return The 'JSON-ize'd State
	 */

	public @Override String toString()
	{
		return toString ("");
	}
}
