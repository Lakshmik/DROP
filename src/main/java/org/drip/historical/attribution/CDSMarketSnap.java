
package org.drip.historical.attribution;

import org.drip.analytics.date.JulianDate;

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
 * <i>CDSMarketSnap</i> contains the Metrics Snapshot associated with the relevant Manifest Measures for the
 * 	given Credit Default Swap Position. It provides the following Functionality:
 *
 *  <ul>
 * 		<li><i>CDSMarketSnap</i> Constructor</li>
 * 		<li>Set the Fair Premium and Position Sensitivity</li>
 * 		<li>Set the Effective Date</li>
 * 		<li>Retrieve the Effective Date</li>
 * 		<li>Set the Maturity Date</li>
 * 		<li>Retrieve the Maturity Date</li>
 * 		<li>Set the Initial Fair Premium</li>
 * 		<li>Retrieve the Initial Fair Premium</li>
 * 		<li>Set the Current Fair Premium</li>
 * 		<li>Retrieve the Current Fair Premium</li>
 * 		<li>Set the Fixed Coupon</li>
 * 		<li>Retrieve the Fixed Coupon</li>
 * 		<li>Set the Clean DV01</li>
 * 		<li>Retrieve the Clean DV01</li>
 * 		<li>Set the Roll Down Fair Premium</li>
 * 		<li>Retrieve the Roll Down Fair Premium</li>
 * 		<li>Set the Accrued</li>
 * 		<li>Retrieve the Accrued</li>
 * 		<li>Set the Cumulative Coupon Amount</li>
 * 		<li>Retrieve the Cumulative Coupon Amount</li>
 * 		<li>Set the Credit Label</li>
 * 		<li>Retrieve the Credit Label</li>
 * 		<li>Set the Recovery Rate</li>
 * 		<li>Retrieve the Recovery Rate</li>
 * 		<li>Set the Coupon PV</li>
 * 		<li>Retrieve the Coupon PV</li>
 * 		<li>Set the Loss PV</li>
 * 		<li>Retrieve the Loss PV</li>
 *  </ul>
 *  
 *	<br>
 *  <table style="border:1px solid black;margin-left:auto;margin-right:auto;">
 *		<tr><td><b>Module </b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationalCore.md">Computational Core Module</a></td></tr>
 *		<tr><td><b>Library</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/ComputationSupportLibrary.md">Computation Support</a></td></tr>
 *		<tr><td><b>Project</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/README.md">Historical State Processing Utilities</a></td></tr>
 *		<tr><td><b>Package</b></td> <td><a href = "https://github.com/lakshmiDRIP/DROP/tree/master/src/main/java/org/drip/historical/attribution/README.md">Position Market Change Components Attribution</a></td></tr>
 *  </table>
 *	<br>
 *
 * @author Lakshmi Krishnamurthy
 */

public class CDSMarketSnap
	extends PositionMarketSnap
{

	/**
	 * <i>CDSMarketSnap</i> Constructor
	 * 
	 * @param snapDate The Snapshot Date
	 * @param marketValue The Snapshot Market Value
	 * 
	 * @throws Exception Thrown if the Inputs are Invalid
	 */

	public CDSMarketSnap (
		final JulianDate snapDate,
		final double marketValue)
		throws Exception
	{
		super (snapDate, marketValue);
	}

	/**
	 * Set the Fair Premium and Position Sensitivity
	 * 
	 * @param fairPremium The Fair Premium
	 * @param fairPremiumSensitivity The Position Fair Premium Sensitivity
	 * @param fairPremiumRollDown The Position Fair Premium Roll Down
	 * 
	 * @return TRUE - The Fair Premium and the Position Sensitivity successfully set
	 */

	public boolean setFairPremiumMarketFactor (
		final double fairPremium,
		final double fairPremiumSensitivity,
		final double fairPremiumRollDown)
	{
		return addManifestMeasureSnap (
			"FairPremium",
			fairPremium,
			fairPremiumSensitivity,
			fairPremiumRollDown
		);
	}

	/**
	 * Set the Effective Date
	 * 
	 * @param effectiveDate The Effective Date
	 * 
	 * @return TRUE - The Effective Date successfully set
	 */

	public boolean setEffectiveDate (
		final JulianDate effectiveDate)
	{
		return setDate ("EffectiveDate", effectiveDate);
	}

	/**
	 * Retrieve the Effective Date
	 * 
	 * @return The Effective Date
	 */

	public JulianDate effectiveDate()
	{
		return date ("EffectiveDate");
	}

	/**
	 * Set the Maturity Date
	 * 
	 * @param maturityDate The Maturity Date
	 * 
	 * @return TRUE - The Maturity Date successfully set
	 */

	public boolean setMaturityDate (
		final JulianDate maturityDate)
	{
		return setDate ("MaturityDate", maturityDate);
	}

	/**
	 * Retrieve the Maturity Date
	 * 
	 * @return The Maturity Date
	 */

	public JulianDate maturityDate()
	{
		return date ("MaturityDate");
	}

	/**
	 * Set the Initial Fair Premium
	 * 
	 * @param initialFairPremium The Initial Fair Premium
	 * 
	 * @return TRUE - The Initial Fair Premium Successfully set
	 */

	public boolean setInitialFairPremium (
		final double initialFairPremium)
	{
		return setR1 ("InitialFairPremium", initialFairPremium);
	}

	/**
	 * Retrieve the Initial Fair Premium
	 * 
	 * @return The Initial Fair Premium
	 * 
	 * @throws Exception Thrown if the Initial Fair Premium cannot be obtained
	 */

	public double initialFairPremium()
		throws Exception
	{
		return r1 ("InitialFairPremium");
	}

	/**
	 * Set the Current Fair Premium
	 * 
	 * @param currentFairPremium The Current Fair Premium
	 * 
	 * @return TRUE - The Current Fair Premium Successfully Set
	 */

	public boolean setCurrentFairPremium (
		final double currentFairPremium)
	{
		return setR1 ("CurrentFairPremium", currentFairPremium);
	}

	/**
	 * Retrieve the Current Fair Premium
	 * 
	 * @return The Current Fair Premium
	 * 
	 * @throws Exception Thrown if the Current Fair Premium cannot be obtained
	 */

	public double currentFairPremium()
		throws Exception
	{
		return r1 ("CurrentFairPremium");
	}

	/**
	 * Set the Fixed Coupon
	 * 
	 * @param fixedCoupon The Fixed Coupon
	 * 
	 * @return TRUE - The Fixed Coupon Successfully Set
	 */

	public boolean setFixedCoupon (
		final double fixedCoupon)
	{
		return setR1 ("FixedCoupon", fixedCoupon);
	}

	/**
	 * Retrieve the Fixed Coupon
	 * 
	 * @return The Fixed Coupon
	 * 
	 * @throws Exception Thrown if the Fixed Coupon cannot be obtained
	 */

	public double fixedCoupon()
		throws Exception
	{
		return r1 ("FixedCoupon");
	}

	/**
	 * Set the Clean DV01
	 * 
	 * @param cleanDV01 The Clean DV01
	 * 
	 * @return TRUE - The Clean DV01 Successfully Set
	 */

	public boolean setCleanDV01 (
		final double cleanDV01)
	{
		return setR1 ("CleanDV01", cleanDV01);
	}

	/**
	 * Retrieve the Clean DV01
	 * 
	 * @return The Clean DV01
	 * 
	 * @throws Exception Thrown if the Clean DV01 cannot be obtained
	 */

	public double cleanDV01()
		throws Exception
	{
		return r1 ("CleanDV01");
	}

	/**
	 * Set the Roll Down Fair Premium
	 * 
	 * @param rollDownFairPremium The Roll Down Fair Premium
	 * 
	 * @return TRUE - The Roll Down Fair Premium Successfully Set
	 */

	public boolean setRollDownFairPremium (
		final double rollDownFairPremium)
	{
		return setR1 ("RollDownFairPremium", rollDownFairPremium);
	}

	/**
	 * Retrieve the Roll Down Fair Premium
	 * 
	 * @return The Roll Down Fair Premium
	 * 
	 * @throws Exception Thrown if the Roll Down Fair Premium cannot be obtained
	 */

	public double rollDownFairPremium()
		throws Exception
	{
		return r1 ("RollDownFairPremium");
	}

	/**
	 * Set the Accrued
	 * 
	 * @param accrued The Accrued
	 * 
	 * @return TRUE - The Accrued successfully set
	 */

	public boolean setAccrued (
		final double accrued)
	{
		return setR1 ("Accrued", accrued);
	}

	/**
	 * Retrieve the Accrued
	 * 
	 * @return The Accrued
	 * 
	 * @throws Exception Thrown if the Accrued cannot be obtained
	 */

	public double accrued()
		throws Exception
	{
		return r1 ("Accrued");
	}

	/**
	 * Set the Cumulative Coupon Amount
	 * 
	 * @param cumulativeCouponAmount The Cumulative Coupon Amount
	 * 
	 * @return TRUE - The Cumulative Coupon Amount successfully set
	 */

	public boolean setCumulativeCouponAmount (
		final double cumulativeCouponAmount)
	{
		return setR1 ("CumulativeCouponAmount", cumulativeCouponAmount);
	}

	/**
	 * Retrieve the Cumulative Coupon Amount
	 * 
	 * @return The Cumulative Coupon Amount
	 * 
	 * @throws Exception Thrown if the Cumulative Coupon Amount cannot be obtained
	 */

	public double cumulativeCouponAmount()
		throws Exception
	{
		return r1 ("CumulativeCouponAmount");
	}

	/**
	 * Set the Credit Label
	 * 
	 * @param creditLabel Credit Label
	 * 
	 * @return TRUE - The Credit Label successfully set
	 */

	public boolean setCreditLabel (
		final String creditLabel)
	{
		return setC1 ("CreditLabel", creditLabel);
	}

	/**
	 * Retrieve the Credit Label
	 * 
	 * @return The Credit Label
	 */

	public String creditLabel()
	{
		return c1 ("CreditLabel");
	}

	/**
	 * Set the Recovery Rate
	 * 
	 * @param recoveryRate The Recovery Rate
	 * 
	 * @return TRUE - The Recovery Rate successfully set
	 */

	public boolean setRecoveryRate (
		final double recoveryRate)
	{
		return setR1 ("RecoveryRate", recoveryRate);
	}

	/**
	 * Retrieve the Recovery Rate
	 * 
	 * @return The Recovery Rate
	 * 
	 * @throws Exception Thrown if the Recovery Rate cannot be obtained
	 */

	public double recoveryRate()
		throws Exception
	{
		return r1 ("RecoveryRate");
	}

	/**
	 * Set the Coupon PV
	 * 
	 * @param dblCouponPV The Coupon PV
	 * 
	 * @return TRUE - The Coupon PV successfully set
	 */

	public boolean setCouponPV (
		final double dblCouponPV)
	{
		return setR1 ("CouponPV", dblCouponPV);
	}

	/**
	 * Retrieve the Coupon PV
	 * 
	 * @return The Coupon PV
	 * 
	 * @throws Exception Thrown if the Coupon PV cannot be obtained
	 */

	public double couponPV()
		throws Exception
	{
		return r1 ("CouponPV");
	}

	/**
	 * Set the Loss PV
	 * 
	 * @param lossPV The Loss PV
	 * 
	 * @return TRUE - The Loss PV successfully set
	 */

	public boolean setLossPV (
		final double lossPV)
	{
		return setR1 ("LossPV", lossPV);
	}

	/**
	 * Retrieve the Loss PV
	 * 
	 * @return The Loss PV
	 * 
	 * @throws Exception Thrown if the Loss PV cannot be obtained
	 */

	public double lossPV()
		throws Exception
	{
		return r1 ("LossPV");
	}
}
