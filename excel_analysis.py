#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Excel数据分析工具
使用markitdown将Excel文件转换为Markdown格式，便于分析和处理
"""

from markitdown import MarkItDown
import pandas as pd
import os
import sys
from pathlib import Path

class ExcelAnalyzer:
    def __init__(self):
        """初始化Excel分析器"""
        self.markitdown = MarkItDown()
    
    def convert_excel_to_markdown(self, excel_path):
        """
        将Excel文件转换为Markdown格式
        
        Args:
            excel_path (str): Excel文件路径
            
        Returns:
            str: 转换后的Markdown内容
        """
        try:
            if not os.path.exists(excel_path):
                raise FileNotFoundError(f"文件不存在: {excel_path}")
            
            print(f"正在转换文件: {excel_path}")
            result = self.markitdown.convert(excel_path)
            
            return result.text_content
        
        except Exception as e:
            print(f"转换失败: {str(e)}")
            return None
    
    def analyze_excel_with_pandas(self, excel_path):
        """
        使用pandas分析Excel文件
        
        Args:
            excel_path (str): Excel文件路径
            
        Returns:
            dict: 分析结果
        """
        try:
            # 读取Excel文件
            df = pd.read_excel(excel_path)
            
            analysis_result = {
                '文件名': os.path.basename(excel_path),
                '数据形状': f"行数: {df.shape[0]}, 列数: {df.shape[1]}",
                '列名': list(df.columns),
                '数据类型': df.dtypes.to_dict(),
                '基本统计': df.describe().to_dict() if not df.empty else "无数据",
                '缺失值': df.isnull().sum().to_dict(),
            }
            
            return analysis_result
            
        except Exception as e:
            print(f"分析失败: {str(e)}")
            return None
    
    def save_markdown_report(self, content, output_path):
        """
        保存Markdown报告
        
        Args:
            content (str): Markdown内容
            output_path (str): 输出文件路径
        """
        try:
            with open(output_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"报告已保存至: {output_path}")
        except Exception as e:
            print(f"保存失败: {str(e)}")

def main():
    """主函数"""
    analyzer = ExcelAnalyzer()
    
    # 示例用法
    print("Excel数据分析工具")
    print("=================")
    
    # 检查是否有命令行参数
    if len(sys.argv) > 1:
        excel_file = sys.argv[1]
    else:
        # 提示用户输入文件路径
        excel_file = input("请输入Excel文件路径: ").strip()
    
    if not excel_file:
        print("未提供文件路径")
        return
    
    # 转换为Markdown
    print("\n1. 使用MarkItDown转换Excel为Markdown:")
    markdown_content = analyzer.convert_excel_to_markdown(excel_file)
    
    if markdown_content:
        print("转换成功！")
        print("前100个字符预览:")
        print(markdown_content[:100] + "..." if len(markdown_content) > 100 else markdown_content)
        
        # 保存Markdown报告
        output_file = f"{Path(excel_file).stem}_report.md"
        analyzer.save_markdown_report(markdown_content, output_file)
    
    # 使用pandas分析
    print("\n2. 使用Pandas进行数据分析:")
    analysis = analyzer.analyze_excel_with_pandas(excel_file)
    
    if analysis:
        print("分析结果:")
        for key, value in analysis.items():
            print(f"  {key}: {value}")

if __name__ == "__main__":
    main()