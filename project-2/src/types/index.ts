export interface Entry {
  id: number;
  amount: number;
  description: string;
  date: string;
}

export interface MessageProps {
  text: string;
  type: 'success' | 'error';
  visible: boolean;
}