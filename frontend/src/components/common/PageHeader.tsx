import { Typography } from 'antd';

const { Title, Text } = Typography;

interface Props {
  title: string;
  subtitle?: string;
  extra?: React.ReactNode;
}

export default function PageHeader({ title, subtitle, extra }: Props) {
  return (
    <div className="flex items-start justify-between mb-6">
      <div>
        <Title level={4} style={{ marginBottom: subtitle ? 4 : 0 }}>
          {title}
        </Title>
        {subtitle && (
          <Text type="secondary" className="text-sm">
            {subtitle}
          </Text>
        )}
      </div>
      {extra && <div className="flex items-center gap-2">{extra}</div>}
    </div>
  );
}
