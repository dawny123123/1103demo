#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Excel数据分析工具
使用方法: python3 excel_analyzer.py [Excel文件路径]
"""

import sys
import os
from pathlib import Path

# 添加虚拟环境的包路径
sys.path.insert(0, '/Users/yuxiao/Downloads/0713demo /myenv/lib/python3.13/site-packages')

def analyze_excel(file_path):
    """分析Excel文件的主函数"""
    try:
        from markitdown import MarkItDown
        import pandas as pd
        import warnings
        warnings.filterwarnings('ignore')
        
        print("🎯 Excel数据分析工具")
        print("=" * 50)
        print(f"📁 分析文件: {file_path}")
        print()
        
        # 检查文件是否存在
        if not os.path.exists(file_path):
            print(f"❌ 文件不存在: {file_path}")
            return
        
        # 第一步：MarkItDown转换
        print("🔄 步骤1: 使用MarkItDown转换Excel为Markdown格式")
        print("-" * 40)
        try:
            md = MarkItDown()
            result = md.convert(file_path)
            print("✅ 转换成功!")
            print()
            print("📋 表格内容:")
            print(result.text_content)
            print()
        except Exception as e:
            print(f"❌ MarkItDown转换失败: {str(e)}")
            print()
        
        # 第二步：Pandas详细分析
        print("📊 步骤2: 使用Pandas进行数据分析")
        print("-" * 40)
        
        try:
            # 读取Excel文件
            excel_file = pd.ExcelFile(file_path)
            sheet_names = excel_file.sheet_names
            
            print(f"📋 工作表数量: {len(sheet_names)}")
            print(f"工作表名称: {', '.join(sheet_names)}")
            print()
            
            for sheet_name in sheet_names:
                print(f"🔍 分析工作表: {sheet_name}")
                print("-" * 20)
                
                df = pd.read_excel(file_path, sheet_name=sheet_name)
                
                # 基本信息
                print(f"• 数据形状: {df.shape[0]} 行 × {df.shape[1]} 列")
                print(f"• 列名: {', '.join(df.columns)}")
                
                # 检查缺失值
                missing_values = df.isnull().sum()
                if missing_values.sum() > 0:
                    print("⚠️  发现缺失值:")
                    for col, count in missing_values.items():
                        if count > 0:
                            percentage = (count / len(df)) * 100
                            print(f"    {col}: {count} 个 ({percentage:.1f}%)")
                else:
                    print("✅ 无缺失值")
                
                # 数值列分析
                numeric_cols = df.select_dtypes(include=['number']).columns
                if len(numeric_cols) > 0:
                    print("\\n📈 数值列分析:")
                    for col in numeric_cols:
                        stats = df[col].describe()
                        print(f"    {col}:")
                        print(f"      平均值: {stats['mean']:.2f}")
                        print(f"      中位数: {stats['50%']:.2f}")
                        print(f"      标准差: {stats['std']:.2f}")
                        print(f"      范围: {stats['min']:.2f} ~ {stats['max']:.2f}")
                
                # 文本列分析
                text_cols = df.select_dtypes(include=['object']).columns
                if len(text_cols) > 0:
                    print("\\n📝 文本列分析:")
                    for col in text_cols:
                        unique_count = df[col].nunique()
                        print(f"    {col}: {unique_count} 个唯一值")
                        
                        # 显示最常见的值
                        if unique_count <= 10:
                            top_values = df[col].value_counts().head(3)
                            print("      最常见的值:")
                            for value, count in top_values.items():
                                percentage = (count / len(df)) * 100
                                print(f"        '{value}': {count} 次 ({percentage:.1f}%)")
                        else:
                            top_values = df[col].value_counts().head(3)
                            print("      前3个最常见值:")
                            for value, count in top_values.items():
                                percentage = (count / len(df)) * 100
                                print(f"        '{value}': {count} 次 ({percentage:.1f}%)")
                
                print()
            
        except Exception as e:
            print(f"❌ Pandas分析失败: {str(e)}")
        
        # 第三步：生成报告
        print("📄 步骤3: 生成分析报告")
        print("-" * 40)
        
        try:
            report_name = f"{Path(file_path).stem}_analysis_report.md"
            
            # 创建报告内容
            report_content = f"""# Excel数据分析报告

## 文件信息
- **文件名**: {file_path}
- **分析时间**: {pd.Timestamp.now().strftime('%Y-%m-%d %H:%M:%S')}

## MarkItDown转换结果

{result.text_content if 'result' in locals() else '转换失败'}

## 数据分析摘要

"""
            
            # 添加每个工作表的分析结果
            for sheet_name in sheet_names:
                df = pd.read_excel(file_path, sheet_name=sheet_name)
                report_content += f"### {sheet_name}\n"
                report_content += f"- 数据量: {df.shape[0]} 行 × {df.shape[1]} 列\n"
                report_content += f"- 列名: {', '.join(df.columns)}\n"
                
                # 数值列统计
                numeric_cols = df.select_dtypes(include=['number']).columns
                if len(numeric_cols) > 0:
                    report_content += "\\n**数值列统计:**\\n"
                    for col in numeric_cols:
                        stats = df[col].describe()
                        report_content += f"- {col}: 均值={stats['mean']:.2f}, 标准差={stats['std']:.2f}\\n"
                
                report_content += "\\n"
            
            # 保存报告
            with open(report_name, 'w', encoding='utf-8') as f:
                f.write(report_content)
            
            print(f"✅ 报告已保存: {report_name}")
            
        except Exception as e:
            print(f"❌ 生成报告失败: {str(e)}")
        
        print("\\n🎉 分析完成!")
        print("=" * 50)
        
    except ImportError as e:
        print(f"❌ 依赖包导入失败: {str(e)}")
        print("请确保已安装所需的Python包")
    except Exception as e:
        print(f"❌ 分析过程中发生错误: {str(e)}")

def main():
    """主程序入口"""
    print("🚀 Excel数据分析工具")
    print("支持使用MarkItDown和Pandas进行综合分析")
    print()
    
    # 获取文件路径
    if len(sys.argv) > 1:
        file_path = sys.argv[1]
    else:
        # 检查当前目录下的Excel文件
        excel_files = []
        for ext in ['*.xlsx', '*.xls']:
            excel_files.extend(Path('.').glob(ext))
        
        if excel_files:
            print("📁 发现以下Excel文件:")
            for i, file in enumerate(excel_files, 1):
                print(f"  {i}. {file.name}")
            
            try:
                choice = input("\\n请选择文件序号 (或直接输入文件路径): ").strip()
                if choice.isdigit():
                    idx = int(choice) - 1
                    if 0 <= idx < len(excel_files):
                        file_path = str(excel_files[idx])
                    else:
                        print("❌ 序号无效")
                        return
                else:
                    file_path = choice
            except KeyboardInterrupt:
                print("\\n👋 已取消")
                return
        else:
            file_path = input("📁 请输入Excel文件路径: ").strip()
    
    if not file_path:
        print("❌ 未提供文件路径")
        return
    
    # 执行分析
    analyze_excel(file_path)

if __name__ == "__main__":
    main()