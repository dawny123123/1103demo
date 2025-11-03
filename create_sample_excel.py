#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
创建示例Excel文件用于测试数据分析功能
"""

import pandas as pd
import numpy as np
from datetime import datetime, timedelta
import random

def create_sample_excel():
    """创建示例Excel文件"""
    
    # 设置随机种子以获得可重现的结果
    np.random.seed(42)
    random.seed(42)
    
    # 创建示例数据
    dates = pd.date_range(start='2024-01-01', end='2024-12-31', freq='D')
    
    # 销售数据
    sales_data = []
    products = ['产品A', '产品B', '产品C', '产品D', '产品E']
    regions = ['北京', '上海', '广州', '深圳', '杭州']
    
    for _ in range(200):
        record = {
            '日期': random.choice(dates).date(),
            '产品名称': random.choice(products),
            '地区': random.choice(regions),
            '销售数量': random.randint(10, 100),
            '单价': round(random.uniform(50.0, 500.0), 2),
            '折扣率': round(random.uniform(0, 0.3), 2),
            '销售员': f"销售员{random.randint(1, 10)}",
        }
        record['销售额'] = round(record['销售数量'] * record['单价'] * (1 - record['折扣率']), 2)
        sales_data.append(record)
    
    df_sales = pd.DataFrame(sales_data)
    
    # 客户数据
    customer_data = []
    for i in range(50):
        customer = {
            '客户ID': f'C{i+1:03d}',
            '客户名称': f'客户{i+1}',
            '客户类型': random.choice(['企业', '个人']),
            '年龄': random.randint(20, 65) if random.choice(['企业', '个人']) == '个人' else None,
            '注册日期': random.choice(dates).date(),
            '总消费金额': round(random.uniform(1000, 50000), 2),
            '会员等级': random.choice(['普通', '银牌', '金牌', '钻石']),
        }
        customer_data.append(customer)
    
    df_customers = pd.DataFrame(customer_data)
    
    # 库存数据
    inventory_data = []
    for product in products:
        inventory = {
            '产品名称': product,
            '当前库存': random.randint(50, 500),
            '安全库存': random.randint(20, 100),
            '成本价': round(random.uniform(30, 300), 2),
            '供应商': f'供应商{random.randint(1, 5)}',
            '最后进货日期': random.choice(dates[-30:]).date(),
        }
        inventory_data.append(inventory)
    
    df_inventory = pd.DataFrame(inventory_data)
    
    # 创建Excel文件，包含多个工作表
    with pd.ExcelWriter('sample_data.xlsx', engine='openpyxl') as writer:
        df_sales.to_excel(writer, sheet_name='销售数据', index=False)
        df_customers.to_excel(writer, sheet_name='客户数据', index=False)
        df_inventory.to_excel(writer, sheet_name='库存数据', index=False)
        
        # 创建汇总表
        summary_data = {
            '指标': ['总销售额', '订单数量', '客户数量', '产品种类', '平均订单金额'],
            '数值': [
                df_sales['销售额'].sum(),
                len(df_sales),
                len(df_customers),
                len(products),
                df_sales['销售额'].mean()
            ]
        }
        df_summary = pd.DataFrame(summary_data)
        df_summary.to_excel(writer, sheet_name='数据汇总', index=False)
    
    print("示例Excel文件已创建: sample_data.xlsx")
    print(f"包含工作表: 销售数据({len(df_sales)}行), 客户数据({len(df_customers)}行), 库存数据({len(df_inventory)}行), 数据汇总")
    
    return 'sample_data.xlsx'

if __name__ == "__main__":
    create_sample_excel()